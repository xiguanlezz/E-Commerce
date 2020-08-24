package com.cj.cn.service;

import com.cj.cn.response.ResultResponse;

public interface ICategoryService {
    ResultResponse addCategory(String categoryName, Integer parentId);

    ResultResponse updateCategoryName(Integer categoryId, String categoryName);

    ResultResponse getParallelChildrenCategory(Integer parentId);

    ResultResponse getDeepChildrenCategory(Integer parentId);
}
