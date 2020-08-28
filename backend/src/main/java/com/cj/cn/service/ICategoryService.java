package com.cj.cn.service;

import com.cj.cn.response.ResultResponse;

public interface ICategoryService {
    /**
     * 增加一个品类
     *
     * @param categoryName 品类名称
     * @param parentId     父品类的id
     * @return
     */
    ResultResponse addCategory(String categoryName, Integer parentId);

    /**
     * 修改品类的名称
     *
     * @param categoryId   品类id
     * @param categoryName 品类名称
     * @return
     */
    ResultResponse updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 得到该品类下一层子品类的信息
     *
     * @param categoryId 品类的id
     * @return
     */
    ResultResponse getParallelChildrenCategory(Integer categoryId);

    /**
     * 递归得到本品类和该品类下所有子节点的id
     *
     * @param categoryId  品类的id
     * @return
     */
    ResultResponse getDeepChildrenCategory(Integer categoryId);
}
