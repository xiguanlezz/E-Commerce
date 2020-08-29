package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.Category;
import com.cj.cn.pojo.Product;
import com.cj.cn.mapper.CategoryMapper;
import com.cj.cn.mapper.ProductMapper;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import com.cj.cn.vo.ProductDetailVO;
import com.cj.cn.vo.ProductListVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResultResponse saveOrUpdateProduct(Product product) {
        if (product == null) {
            return ResultResponse.error("新增或更新产品参数不正确");
        }
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImageArray = product.getSubImages().split(",");
            if (subImageArray.length > 0) {
                //产品的默认主图是产品的第一个子图
                product.setMainImage(subImageArray[0]);
            }
        }
        int rowCount = -1;
        //用productId区分是更新还是新增
        if (product.getId() != null) {
            product.setUpdateTime(LocalDateTime.now());     //重新设置当前时间为最后更新时间
            rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0) {
                return ResultResponse.ok("更新产品成功");
            } else {
                return ResultResponse.error("更新产品失败");
            }

        } else {
            product.setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());  //插入产品的时候设置创建时间和最后更新时间为当前时间
            rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ResultResponse.ok("新增产品成功");
            } else {
                return ResultResponse.error("新增产品失败");
            }
        }
    }

    @Override
    public ResultResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId).setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ResultResponse.ok("修改产品销售状态成功");
        } else {
            return ResultResponse.error("修改产品销售状态失败");
        }
    }

    @Override
    public ResultResponse manageProductDetail(Integer productId) {
        if (productId == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResultResponse.error("产品已下架或删除");
        }
        //简单对象直接用VO, 复杂业务ENTITY -> BO -> VO
        ProductDetailVO productDetailVO = this.copyProductDetailVOByProduct(product);
        return ResultResponse.ok(productDetailVO);
    }

    private ProductDetailVO copyProductDetailVOByProduct(Product product) {
        if (product == null) {
            return null;
        } else {
            ProductDetailVO productDetailVO = new ProductDetailVO();
            productDetailVO.setId(product.getId());
            productDetailVO.setCategoryId(product.getCategoryId());
            productDetailVO.setName(product.getName());
            productDetailVO.setSubtitle(product.getSubtitle());
            productDetailVO.setMainImage(product.getMainImage());
            productDetailVO.setSubImage(product.getSubImages());
            productDetailVO.setDetail(product.getDetail());

            productDetailVO.setPrice(product.getPrice());
            productDetailVO.setStock(product.getStock());
            productDetailVO.setStatus(product.getStatus());

            Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
            if (category != null) {
                productDetailVO.setParentCategoryId(category.getParentId());
            }
            return productDetailVO;
        }
    }

    @Override
    public ResultResponse getProductList(int pageNum, int pageSize) {
        //PageHelper分页使用先start
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVO> productListVOList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVO productListVO = this.copyProductListVOByProduct(product);
            productListVOList.add(productListVO);
        }

        //最后PageHelper再收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ResultResponse.ok(pageResult);
    }

    private ProductListVO copyProductListVOByProduct(Product product) {
        if (product == null) {
            return null;
        } else {
            ProductListVO productListVO = new ProductListVO();
            productListVO.setId(product.getId());
            productListVO.setCategoryId(product.getCategoryId());
            productListVO.setName(product.getName());
            productListVO.setSubtitle(product.getSubtitle());
            productListVO.setMainImage(product.getMainImage());
            productListVO.setPrice(product.getPrice());
            productListVO.setStatus(product.getStatus());
            return productListVO;
        }
    }

    public ResultResponse searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectLikeNameOrByProductId(productName, productId);

        List<ProductListVO> productListVOList = new LinkedList<>();
        for (Product product : productList) {
            ProductListVO productListVO = this.copyProductListVOByProduct(product);
            productListVOList.add(productListVO);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVOList);
        return ResultResponse.ok(pageResult);
    }

    @Override
    public ResultResponse getProductDetail(Integer productId) {
        if (productId == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResultResponse.error("产品已下架或删除");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ResultResponse.error("产品已下架或删除");
        }
        //简单对象直接用VO, 复杂业务POJO -> BO -> VO
        ProductDetailVO productDetailVO = this.copyProductDetailVOByProduct(product);
        return ResultResponse.ok(productDetailVO);
    }

    public ResultResponse getProductByKeywordCategory(String keyword, Integer categoryId) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类并且没有关键字

            }
        }
        return null;
    }
}
