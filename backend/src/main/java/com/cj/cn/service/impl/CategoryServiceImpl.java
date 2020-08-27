package com.cj.cn.service.impl;

import com.cj.cn.pojo.Category;
import com.cj.cn.mapper.CategoryMapper;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
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

    @Override
    public ResultResponse getParallelChildrenCategory(Integer parentId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(parentId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ResultResponse.ok(categoryList);
    }

    @Override
    public ResultResponse getDeepChildrenCategory(Integer parentId) {
        Set<Category> categorySet = new HashSet<>();
        findChildCategory(categorySet, parentId);

        List<Integer> categoryIdList = new ArrayList<>();
        if (parentId != null) {
            for (Category category : categorySet) {
                if (category != null) {
                    categoryIdList.add(category.getId());
                }
            }
        }
        return ResultResponse.ok(categoryIdList);
    }

    /**
     * 递归算法, 查询出本节点信息和所有子节点的信息
     * 方法的设计思想: 先将本节点加入集合, 在查找子节点依次调用本方法
     *
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (categoryId != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = (List<Category>) getParallelChildrenCategory(categoryId).getData();
        for (Category c : categoryList) {
            if (c != null) {
                //递归查找子节点
                findChildCategory(categorySet, c.getId());
            }
        }
        return categorySet;
    }
}
