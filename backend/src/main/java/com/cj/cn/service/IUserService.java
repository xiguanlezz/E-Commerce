package com.cj.cn.service;

import com.cj.cn.entity.User;
import com.cj.cn.response.ResultResponse;

public interface IUserService {
    ResultResponse login(String username, String password);

    ResultResponse register(User user);

    ResultResponse checkValid(String str, String type);
}
