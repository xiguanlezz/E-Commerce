package com.cj.cn.mapper;

import com.cj.cn.entity.Category;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category> {
    List<Category> selectCategoryChildrenByParentId(Integer id);
}