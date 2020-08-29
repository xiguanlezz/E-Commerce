package com.cj.cn.service;

import com.cj.cn.response.ResultResponse;

public interface IOrderService {
    /**
     * 创建订单
     *
     * @param userId     用户id
     * @param shippingId 地址id
     */
    ResultResponse createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     *
     * @param userId  用户id
     * @param orderNo 订单号
     */
    ResultResponse cancel(Integer userId, Long orderNo);

    /**
     * 获取用户勾选的购物车中产品的详细信息
     *
     * @param userId 用户id
     */
    ResultResponse getOrderCartProduct(Integer userId);

    /**
     * 查看订单详情
     *
     * @param userId  用户id
     * @param orderNo 订单号
     */
    ResultResponse getOrderDetail(Integer userId, Long orderNo);

    /**
     * 分页查询订单列表
     *
     * @param userId   用户id
     * @param pageNum  当前页
     * @param pageSize 页容量
     */
    ResultResponse getOrderList(Integer userId, int pageNum, int pageSize);

    /**
     * 后台分页查询订单列表
     *
     * @param pageNum  当前页
     * @param pageSize 页容量
     */
    ResultResponse getManageOrderList(int pageNum, int pageSize);

    /**
     * 后台查看订单详情
     *
     * @param orderNo 订单号
     */
    ResultResponse getManageDetail(Long orderNo);

    /**
     * 后台分页搜索订单
     *
     * @param orderNo  订单号
     * @param pageNum  当前页
     * @param pageSize 页容量
     */
    ResultResponse getManageSearch(Long orderNo, int pageNum, int pageSize);

    /**
     * 订单发货
     *
     * @param orderNo 订单号
     */
    ResultResponse manageSendGoods(Long orderNo);
}
