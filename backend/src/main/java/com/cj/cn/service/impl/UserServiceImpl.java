package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.entity.User;
import com.cj.cn.mapper.UserMapper;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultResponse login(String username, String password) {
        ResultResponse response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            String md5Password = MD5Util.MD5EncodeUtf8(password);   //对密码进行MD5加密之后去库中匹配
            User user = userMapper.selectOne(new User().setUsername(username).setPassword(password));
            if (user == null) {
                return ResultResponse.<User>error("密码错误");
            }
            //删除用户密码返回给前端
            user.setPassword("");
            return ResultResponse.ok("登陆成功", user);
        } else {
            return ResultResponse.error("用户名不存在");
        }
    }

    @Override
    public ResultResponse register(User user) {
        ResultResponse response = checkValid(user.getUsername(), Const.USERNAME);
        if (response.isSuccess()) {
            response = checkValid(user.getEmail(), Const.EMAIL);
            if(response.isSuccess()) {
                user.setRole(Const.Role.ROLE_CUSTOMER);
//                user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));    //将密码加密写入数据库
                int resultCount = userMapper.insert(user);
                if (resultCount == 0) {
                    return ResultResponse.error("注册失败");
                }
                return ResultResponse.ok("注册成功", user);
            } else {
                return ResultResponse.error("Email已存在");
            }
        } else {
            return ResultResponse.error("用户名已存在");
        }
    }

    @Override
    public ResultResponse checkValid(String str, String type) {
        if ("".equals(type)) {
            return ResultResponse.error("参数不合法");
        }
        int resultCount = 0;
        if (Const.USERNAME.equals(type)) {
            resultCount = userMapper.checkUsername(str);
            if (resultCount == 1) {
                return ResultResponse.error("用户名已存在");
            }
        }
        if (Const.EMAIL.equals(type)) {
            resultCount = userMapper.checkEmail(str);
            if (resultCount == 1) {
                return ResultResponse.error("Email已存在");
            }
        }
        return ResultResponse.ok();
    }
}
