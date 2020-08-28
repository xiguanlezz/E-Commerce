package com.cj.cn.service;

import com.cj.cn.pojo.Shipping;
import com.cj.cn.response.ResultResponse;

public interface IShippingService {
    /**
     * 新增一个地址
     *
     * @param userId   用户id
     * @param shipping 新增的地址信息
     */
    ResultResponse add(Integer userId, Shipping shipping);

    /**
     * 删除用户的某个地址
     *
     * @param userId     用户id
     * @param shippingId 要删除的地址id
     */
    ResultResponse del(Integer userId, Integer shippingId);

    /**
     * 修改用户的地址信息
     *
     * @param userId   用户id
     * @param shipping 新的地址信息
     */
    ResultResponse update(Integer userId, Shipping shipping);

    /**
     * 查询用户的某个地址
     *
     * @param userId     用户id
     * @param shippingId 地址id
     */
    ResultResponse select(Integer userId, Integer shippingId);

    /**
     * 分页查询用户的地址
     *
     * @param userId   用户id
     * @param pageNum  当前页
     * @param pageSize 页容量
     */
    ResultResponse list(Integer userId, int pageNum, int pageSize);
}
