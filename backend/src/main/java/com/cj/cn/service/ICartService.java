package com.cj.cn.service;

import com.cj.cn.response.ResultResponse;

public interface ICartService {
    /**
     * 查询用户的购物车信息
     *
     * @param userId 用户id
     */
    ResultResponse list(Integer userId);

    /**
     * 往购物车中增加产品
     *
     * @param userId    用户id
     * @param productId 产品id
     * @param count     产品数量
     */
    ResultResponse add(Integer userId, Integer productId, Integer count);

    /**
     * 购物车更新产品数量
     *
     * @param userId    用户id
     * @param productId 产品id
     * @param count     新数量
     */
    ResultResponse update(Integer userId, Integer productId, Integer count);

    /**
     * 批量删除购物车中的产品
     *
     * @param userId     用户id
     * @param productIds 要删除产品的id
     */
    ResultResponse deleteProduct(Integer userId, String productIds);

    /**
     * 根据productId选择或反选购物车中的产品, 传入null则是全选或全反选
     *
     * @param userId  用户id
     * @param checked 选择的标记位, 1表示选中, 0表示未选择
     */
    ResultResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    /**
     * 获取用户购物车中产品的总数量
     *
     * @param userId 用户id
     */
    ResultResponse getCartProductCount(Integer userId);
}
