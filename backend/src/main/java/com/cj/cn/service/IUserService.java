package com.cj.cn.service;

import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;

public interface IUserService {
    /**
     * 登录接口
     *
     * @param username 用户名
     * @param password 密码
     */
    ResultResponse login(String username, String password);

    /**
     * 注册接口
     *
     * @param user 注册的用户信息
     */
    ResultResponse register(User user);

    /**
     * 校验用户中某些参数的合法性
     *
     * @param type 参数所属的类别
     * @param str  参数值
     */
    ResultResponse checkValid(String type, String str);

    /**
     * 返回用户的密保问题
     *
     * @param username 用户名
     */
    ResultResponse selectQuestion(String username);

    /**
     * 检验密保问题的答案是否正确
     *
     * @param username 用户名
     * @param question 问题内容
     * @param answer   问题答案
     */
    ResultResponse checkAnswer(String username, String question, String answer);

    /**
     * 忘记密码中的重置密码
     *
     * @param username    用户名
     * @param password    新密码
     * @param forgetToken token
     */
    ResultResponse forgetResetPassword(String username, String password, String forgetToken);

    /**
     * 直接修改密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param id          用户id
     */
    ResultResponse resetPassword(String passwordOld, String passwordNew, Integer id);

    /**
     * 更新用户的信息
     *
     * @param user 即将更新的用户信息
     */
    ResultResponse updateInformation(User user);

    /**
     * 检查是否是管理员
     *
     * @param user 用户信息
     */
    ResultResponse checkAdminRole(User user);
}
