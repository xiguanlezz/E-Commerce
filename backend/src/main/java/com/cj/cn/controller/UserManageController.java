package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.entity.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登录请求
     *
     * @param session
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login.do")
    public ResultResponse login(HttpSession session, String username, String password) {
        ResultResponse response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = (User) response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, response.getData());
                return response;
            } else {
                return ResultResponse.error("不是管理员, 无法登录");
            }
        }
        return response;
    }
}
