package com.cj.cn.service;

import com.cj.cn.response.ResultResponse;

public interface ICartService {
    /**
     * 查询用户的购物车信息
     *
     * @param userId 用户id
     */
    ResultResponse list(Integer userId);
}
