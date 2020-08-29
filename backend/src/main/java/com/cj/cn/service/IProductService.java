package com.cj.cn.service;

import com.cj.cn.pojo.Product;
import com.cj.cn.response.ResultResponse;

public interface IProductService {
    /**
     * 更新或新增产品
     *
     * @param product 要更新或新增的产品信息
     * @return
     */
    ResultResponse saveOrUpdateProduct(Product product);

    /**
     * 设置商品的状态(上下架)
     *
     * @param productId 产品id
     * @param status    产品状态
     * @return
     */
    ResultResponse setSaleStatus(Integer productId, Integer status);

    /**
     * 查看产品详情(管理员端), 不管status是什么都可以查到
     *
     * @param productId 产品id
     * @return
     */
    ResultResponse getManageProductDetail(Integer productId);

    /**
     * 得到产品列表(分页后)
     *
     * @param pageNum  当前页数
     * @param pageSize 每页显示的产品数量
     * @return
     */
    ResultResponse getProductList(int pageNum, int pageSize);

    /**
     * 搜索产品
     *
     * @param productName 产品名称
     * @param productId   产品id
     * @param pageNum     当前页数
     * @param pageSize    每页显示的产品数量
     * @return
     */
    ResultResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    /**
     * 查看产品详情, 只能看到status为1的产品详情
     *
     * @param productId 产品id
     * @return
     */
    ResultResponse getProductDetail(Integer productId);

    /**
     * 根据品类id进行精确匹配以及关键字进行模糊匹配查询产品列表(分页+排序)
     *
     * @param keyword    关键字
     * @param categoryId 品类id
     * @param pageNum    当前页
     * @param pageSize   页容量
     * @param orderBy    排序规则
     */
    ResultResponse getProductByKeywordOrCategoryId(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
