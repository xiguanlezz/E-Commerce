package com.cj.cn.service;

import com.cj.cn.entity.User;
import com.cj.cn.response.ResultResponse;

public interface IUserService {
    ResultResponse login(String username, String password);

    ResultResponse register(User user);

    ResultResponse checkValid(String str, String type);

    ResultResponse selectQuestion(String username);

    ResultResponse checkAnswer(String username, String question, String answer);

    ResultResponse forgetResetPassword(String username, String password, String forgetToken);

    ResultResponse resetPassword(String passwordOld, String passwordNew, Integer id);

    ResultResponse updateInformation(User user);
}
