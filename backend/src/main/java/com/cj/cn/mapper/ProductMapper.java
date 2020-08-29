package com.cj.cn.mapper;

import com.cj.cn.pojo.Product;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductMapper extends Mapper<Product> {
    List<Product> selectList();

    List<Product> selectLikeNameOrByProductId(@Param("productName") String productName, @Param("productId") Integer productId);

    List<Product> selectLikeNameAndCategoryIds(@Param("productName") String productName, @Param("categoryIdList") List<Integer> categoryIdList);
}