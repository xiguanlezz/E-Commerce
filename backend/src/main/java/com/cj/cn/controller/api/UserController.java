package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 普通用户登录请求
     */
    @PostMapping("login.do")
    public ResultResponse login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        ResultResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());   //将用户信息加入session中(取出密码后)
        }
        return response;
    }

    /**
     * 退出登录请求
     */
    @PostMapping("logout.do")
    public ResultResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ResultResponse.ok();
    }

    /**
     * 注册请求
     */
    @PostMapping("register.do")
    public ResultResponse register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验参数合法性(用户名或邮箱)
     */
    @PostMapping("check_valid.do")
    public ResultResponse checkValid(@RequestParam("str") String str, @RequestParam("type") String type) {
        ResultResponse response = iUserService.checkValid(str, type);
        if (!response.isSuccess()) {
            return ResultResponse.ok();
        }
        return ResultResponse.error(response.getMsg());
    }

    /**
     * 获取已登录用户的信息
     */
    @PostMapping("get_information.do")
    public ResultResponse getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ResultResponse.ok(user);
        }
        return ResultResponse.error("用户未登录, 无法获取当前用户的信息");
    }

    /**
     * 返回密保问题
     */
    @PostMapping("forget_get_question.do")
    public ResultResponse forgetGetQuestion(@RequestParam("username") String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 忘记密码中检查密保问题的正确性
     */
    @PostMapping("forget_check_answer.do")
    public ResultResponse forgetCheckAnswer(@RequestParam("username") String username,
                                            @RequestParam("question") String question,
                                            @RequestParam("answer") String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码中的重置密码
     */
    @PostMapping("forget_reset_password.do")
    public ResultResponse forgetResetPassword(@RequestParam("username") String username,
                                              @RequestParam("password") String password,
                                              @RequestParam("forgetToken") String forgetToken) {
        return iUserService.forgetResetPassword(username, password, forgetToken);
    }

    /**
     * 登录状态下修改密码
     */
    @PostMapping("reset_password.do")
    public ResultResponse resetPassword(@RequestParam("passwordOld") String passwordOld,
                                        @RequestParam("passwordNew") String passwordNew, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user.getId());
    }

    /**
     * 更新用户信息
     */
    @PostMapping("update_information.do")
    public ResultResponse updateInformation(HttpSession session, User user) {
        User u = (User) session.getAttribute(Const.CURRENT_USER);
        if (u == null) {
            return ResultResponse.error("用户未登录");
        }
        user.setId(u.getId());
        return iUserService.updateInformation(user);
    }
}
