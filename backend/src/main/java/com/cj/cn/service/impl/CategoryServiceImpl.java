package com.cj.cn.service.impl;

import com.cj.cn.entity.Category;
import com.cj.cn.mapper.CategoryMapper;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResultResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ResultResponse.error("添加品类参数错误");
        }

        Category category = new Category();
        //设置品类名字和父品类id以及表示这个品类是可用的
        category.setName(categoryName).setParentId(parentId).setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ResultResponse.ok("添加品类成功");
        } else {
            return ResultResponse.error("添加品类失败");
        }
    }

    @Override
    public ResultResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ResultResponse.error("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId).setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ResultResponse.ok("更新品类名字成功");
        } else {
            return ResultResponse.error("更新品类名字错误");
        }
    }
}
