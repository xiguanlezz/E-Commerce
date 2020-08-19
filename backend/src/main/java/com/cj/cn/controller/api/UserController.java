package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.entity.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    /**
     * 登录请求
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping("login.do")
    public ResultResponse login(String username, String password, HttpSession session) {
        ResultResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 退出登录请求
     * @param session
     * @return
     */
    @GetMapping("logout.do")
    public ResultResponse logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ResultResponse.ok();
    }

    /**
     * 注册请求
     * @param user
     * @return
     */
    @PostMapping("register.do")
    public ResultResponse register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验参数合法性(用户名或邮箱)
     * @param str
     * @param type
     * @return
     */
    public ResultResponse checkValid(String str,String type) {
        return iUserService.checkValid(str,type);
    }
}
