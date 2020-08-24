package com.cj.cn.service;

import com.cj.cn.entity.Product;
import com.cj.cn.response.ResultResponse;

public interface IProductService {
    ResultResponse saveOrUpdateProduct(Product product);

    ResultResponse setSaleStatus(Integer productId, Integer status);
}
