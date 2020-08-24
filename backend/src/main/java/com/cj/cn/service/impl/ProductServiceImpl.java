package com.cj.cn.service.impl;

import com.cj.cn.entity.Category;
import com.cj.cn.entity.Product;
import com.cj.cn.mapper.CategoryMapper;
import com.cj.cn.mapper.ProductMapper;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import com.cj.cn.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0) {
                return ResultResponse.ok("更新产品成功");
            } else {
                return ResultResponse.error("更新产品失败");
            }

        } else {
            rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ResultResponse.ok("新增产品成功");
            } else {
                return ResultResponse.error("新增产品失败");
            }
        }
    }

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

    public ResultResponse manageProductDetail(Integer productId) {
        if (productId == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResultResponse.error("产品已下架或删除");
        }
        //简单对象直接用VO, 复杂业务ENTITY -> BO -> VO
//        ProductDetailVO productDetailVO
//        if (product != null) {
//            Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
//            Integer parentId = category.getParentId();
//        }
        //TODO 转为VO对象
        return null;
    }
}
