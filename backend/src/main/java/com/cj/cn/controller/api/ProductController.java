package com.cj.cn.controller.api;

import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @ApiOperation(value = "查看商品详情接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;通过id查看商品详情")
    @ApiImplicitParam(name = "productId", value = "产品id")
    @GetMapping("detail.do")
    public ResultResponse detail(@RequestParam("productId") Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    @ApiOperation(value = "根据关键字和id查询产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;通过关键字进行模糊匹配, id进行精确匹配")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键字"),
            @ApiImplicitParam(name = "categoryId", value = "品类id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量"),
            @ApiImplicitParam(name = "orderBy", value = "排序规则")
    })
    @GetMapping("list.do")
    public ResultResponse list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeywordOrCategoryId(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
