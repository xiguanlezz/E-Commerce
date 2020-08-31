package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.Product;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.FastDFSClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "后台产品模块")
@RestController
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private FastDFSClientUtil fastDFSClient;

    @ApiOperation(value = "新增产品和更新产品信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台新增产品和更新产品信息的统一接口")
    @PostMapping("save.do")
    public ResultResponse productSave(Product product, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "上下架产品的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上架下架商品的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "产品id"),
            @ApiImplicitParam(name = "status", value = "要设置的产品状态")
    })
    @PostMapping("set_sale_status.do")
    public ResultResponse setSaleStatus(@RequestParam("productId") Integer productId,
                                        @RequestParam("status") Integer status,
                                        HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "查看商品详情接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台查看商品详情(上架状态、下架状态都可看到)")
    @ApiImplicitParam(name = "productId", value = "商品id")
    @PostMapping("detail.do")
    public ResultResponse getDetail(@RequestParam("productId") Integer productId,
                                    HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getManageProductDetail(productId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "查看商品列表接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量")
    })
    @PostMapping("list.do")
    public ResultResponse getList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                  HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "搜索产品接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台分页查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名"),
            @ApiImplicitParam(name = "productId", value = "产品id"),
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量")
    })
    @PostMapping("search.do")
    public ResultResponse productSearch(@RequestParam(value = "productName", defaultValue = "") String productName,
                                        @RequestParam(value = "productId", required = false) Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "上传图片接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上传产品图片到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件")
    @PostMapping("upload.do")
    public ResultResponse upload(@RequestParam(value = "upload_file") MultipartFile file,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = fastDFSClient.uploadFile(file);
            if ("".equals(path)) {
                return ResultResponse.error("上传文件失败");
            } else {
                return ResultResponse.ok(path);
            }
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "删除图片接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台上传产品图片到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件")
    @DeleteMapping("unUpload.do")
    public ResultResponse unUpload(@RequestParam(value = "filePath") String path,
                                   HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            fastDFSClient.deleteFile(path);
            return ResultResponse.ok();
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "富文本中图片上传接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台富文本中图片上传到服务器")
    @ApiImplicitParam(name = "upload_file", value = "待上传的图片文件")
    @PostMapping("richtext_img_upload.do")
    public Map<String, Object> richtextImgUpload(@RequestParam(value = "upload_file") MultipartFile file,
                                                 HttpServletResponse response,
                                                 HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录, 请登录");
            return resultMap;
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //富文本中对于返回值有自己的要求, 使用的是simditor需要按照simditor的要求进行返回
            String path = fastDFSClient.uploadFile(file);
            if ("".equals(path)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
            } else {
                resultMap.put("success", true);
                resultMap.put("msg", "上传成功");
                resultMap.put("file_path", path);
                response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            }
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作, 需要管理员权限");
        }
        return resultMap;
    }
}
