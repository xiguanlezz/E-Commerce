package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "前台用户模块")
@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @ApiOperation(value = "登录接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query")
    })
    @PostMapping("login.do")
    public ResultResponse login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session) {
        ResultResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());   //将用户信息加入session中(取出密码后)
        }
        return response;
    }

    @ApiOperation(value = "退出接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户退出接口")
    @PostMapping("logout.do")
    public ResultResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ResultResponse.ok();
    }

    @ApiOperation(value = "注册接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户注册接口")
    @PostMapping("register.do")
    public ResultResponse register(User user) {
        return iUserService.register(user);
    }

    @ApiOperation(value = "校验参数合法性接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用于判断用户名或邮箱是否已注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "要校验的参数类型, username表示校验的是用户名, email表示校验的是邮箱", paramType = "query"),
            @ApiImplicitParam(name = "str", value = "要校验的参数值", paramType = "query")
    })
    @PostMapping("check_valid.do")
    public ResultResponse checkValid(@RequestParam("type") String type,
                                     @RequestParam("str") String str) {
        ResultResponse response = iUserService.checkValid(str, type);
        if (!response.isSuccess()) {
            return ResultResponse.ok();
        }
        return ResultResponse.error(response.getMsg());
    }

    @ApiOperation(value = "登录状态下获取当前用户信息接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台已登录用户获取自己的用户信息")
    @PostMapping("get_information.do")
    public ResultResponse getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ResultResponse.ok(user);
        }
        return ResultResponse.error("用户未登录, 无法获取当前用户的信息");
    }

    @ApiOperation(value = "获取密保问题接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户根据用户名查询对应的密保问题")
    @ApiImplicitParam(name = "username", value = "用户名", paramType = "query")
    @PostMapping("forget_get_question.do")
    public ResultResponse forgetGetQuestion(@RequestParam("username") String username) {
        return iUserService.selectQuestion(username);
    }

    @ApiOperation(value = "校验密保问题答案的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 判断根据用户名、密保问题和问题答案是否正确匹配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "question", value = "密保问题", paramType = "query"),
            @ApiImplicitParam(name = "answer", value = "密保问题的答案", paramType = "query")
    })
    @PostMapping("forget_check_answer.do")
    public ResultResponse forgetCheckAnswer(@RequestParam("username") String username,
                                            @RequestParam("question") String question,
                                            @RequestParam("answer") String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @ApiOperation(value = "根据token重置密码的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 根据token直接重置密码(不需要输入原来的密码)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "新密码", paramType = "query"),
            @ApiImplicitParam(name = "forgetToken", value = "忘记密码中密保问题回答正确后返回的token", paramType = "query")
    })
    @PostMapping("forget_reset_password.do")
    public ResultResponse forgetResetPassword(@RequestParam("username") String username,
                                              @RequestParam("password") String password,
                                              @RequestParam("forgetToken") String forgetToken) {
        return iUserService.forgetResetPassword(username, password, forgetToken);
    }

    @ApiOperation(value = "登录状态下修改密码的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台忘记密码中, 先正确输入原始密码后再设置新密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "passwordOld", value = "原始密码", paramType = "query"),
            @ApiImplicitParam(name = "passwordNew", value = "新密码", paramType = "query"),
    })
    @PostMapping("reset_password.do")
    public ResultResponse resetPassword(@RequestParam("passwordOld") String passwordOld,
                                        @RequestParam("passwordNew") String passwordNew,
                                        HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user.getId());
    }

    @ApiOperation(value = "登录状态下更新用户信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前台用户中心更新用户基本信息")
    @PostMapping("update_information.do")
    public ResultResponse updateInformation(User user, HttpSession session) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ResultResponse.error("用户未登录");
        }
        user.setId(u.getId());
        return iUserService.updateInformation(user);
    }
}
