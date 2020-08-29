package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.common.TokenCache;
import com.cj.cn.pojo.User;
import com.cj.cn.mapper.UserMapper;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultResponse login(String username, String password) {
        ResultResponse response = this.checkValid(Const.USERNAME, username);
        if (response.isSuccess()) {   //用户名存在方可登录
            String md5Password = MD5Util.MD5EncodeUtf8(password);   //对密码进行MD5加密之后去库中匹配
            User user = userMapper.selectOne(new User().setUsername(username).setPassword(password));
            if (user == null) {
                return ResultResponse.error("密码错误");
            }
            user.setPassword("");    //删除用户密码返回给前端
            return ResultResponse.ok("登陆成功", user);
        } else {
            return ResultResponse.error("用户名不存在");
        }
    }

    @Override
    public ResultResponse register(User user) {
        ResultResponse response = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {    //用户名必须唯一
            response = this.checkValid(user.getEmail(), Const.EMAIL);
            if (!response.isSuccess()) {    //邮箱必须唯一
                user.setRole(Const.Role.ROLE_CUSTOMER).setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
//                user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));    //将密码加密写入数据库
                int resultCount = userMapper.insert(user);
                if (resultCount == 0) {
                    return ResultResponse.error("注册失败");
                } else {
                    return ResultResponse.ok("注册成功", user);
                }
            } else {
                return response;
            }
        } else {
            return response;
        }
    }

    @Override
    public ResultResponse checkValid(String type, String str) {
        if (StringUtils.isBlank(type)) {
            return ResultResponse.error("参数不合法");
        }

        int resultCount = 0;
        if (Const.USERNAME.equals(type)) {
            resultCount = userMapper.checkUsername(str);
            if (resultCount == 1) {
                return ResultResponse.ok("用户名已存在");
            }
        }
        if (Const.EMAIL.equals(type)) {
            resultCount = userMapper.checkEmail(str);
            if (resultCount == 1) {
                return ResultResponse.ok("Email已存在");
            }
        }
        return ResultResponse.error("查无此人");
    }

    @Override
    public ResultResponse selectQuestion(String username) {
        ResultResponse response = this.checkValid(Const.USERNAME, username);
        if (response.isSuccess()) {
            String question = userMapper.selectQuestionByUsername(username);
            if (StringUtils.isNotBlank(question)) {
                return ResultResponse.ok(question);
            } else {
                return ResultResponse.ok("找回密码的问题是空的");
            }
        } else {
            return ResultResponse.error(response.getMsg());
        }
    }

    @Override
    public ResultResponse checkAnswer(String username, String question, String answer) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(question) || StringUtils.isBlank(answer)) {
            return ResultResponse.error("参数不正确");
        }

        Integer result = userMapper.checkAnswer(username, question, answer);
        if (result > 0) {
            String forgetToken = UUID.randomUUID().toString();    //密保问题回答正确后会重新设置token并返回
            TokenCache.setKey("token_" + username, forgetToken);
            return ResultResponse.ok(forgetToken);
        } else {
            return ResultResponse.error("问题错误或者答案错误");
        }
    }

    @Override
    public ResultResponse forgetResetPassword(String username, String password, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ResultResponse.error("参数错误, 需要传递token");
        }
        ResultResponse response = this.checkValid(Const.USERNAME, username);
        if (!response.isSuccess()) {
            return ResultResponse.error(response.getMsg());
        }

        String token = TokenCache.getKey("token_" + username);
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            int rowCount = userMapper.updatePasswordByUsername(username, password);
            if (rowCount > 0) {
                return ResultResponse.ok("修改密码成功");
            }
        } else {
            return ResultResponse.ok("token错误, 请重新获取重置密码的token");
        }
        return ResultResponse.ok("修改密码失败");
    }

    @Override
    public ResultResponse resetPassword(String passwordOld, String passwordNew, Integer id) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id).andEqualTo("password", passwordOld);  //拼接查询条件
        int updateCount = userMapper.updateByExampleSelective(new User().setPassword(passwordNew).setUpdateTime(LocalDateTime.now()), example);
        if (updateCount > 0) {
            return ResultResponse.ok("密码修改成功");
        } else {
            return ResultResponse.error("修改密码失败");
        }
    }

    @Override
    public ResultResponse updateInformation(User user) {
        Example example = new Example(User.class);
        example.selectProperties("id", "email");    //设置要查询的字段
        Example.Criteria criteria = example.createCriteria();
        String email = user.getEmail();
        criteria.andEqualTo("email", email);
        List<User> users = userMapper.selectByExample(example);
        for (User u : users) {
            if (StringUtils.equals(email, u.getEmail())) {  //当更新用户的邮箱信息的时候需要查看该邮箱是否被别人注册过了
                return ResultResponse.error("此邮箱已被注册");
            }
        }
        user.setUpdateTime(LocalDateTime.now());    //将当前时间设置为最后更新时间
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ResultResponse.ok("更新用户信息成功");
        } else {
            return ResultResponse.error("更新用户信息失败");
        }
    }

    @Override
    public ResultResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ResultResponse.ok();
        }
        return ResultResponse.error("非管理员");
    }
}
