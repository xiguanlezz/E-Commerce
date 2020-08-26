package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.entity.Product;
import com.cj.cn.entity.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IProductService;
import com.cj.cn.service.IUserService;
import com.cj.cn.util.FastDFSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/product/")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private FastDFSClientUtil fastDFSClient;

    @RequestMapping("save.do")
    public ResultResponse productSave(HttpSession session, Product product) {
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

    @RequestMapping("set_sale_status.do")
    public ResultResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
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

    @RequestMapping("detail.do")
    public ResultResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @RequestMapping("list.do")
    public ResultResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @RequestMapping("search.do")
    public ResultResponse productSearch(HttpSession session, String productName, Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
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

    @RequestMapping("upload.do")
    public ResultResponse upload(@RequestParam(value = "upload_file") MultipartFile file, HttpServletRequest request) {
        String path = fastDFSClient.uploadBase64(file);
        if ("".equals(path)) {
            return ResultResponse.error("上传文件失败");
        } else {
            return ResultResponse.ok(path);
        }
    }

    /*
    @RequestMapping("unUpload.do")
    public ResultResponse unUpload(@RequestParam(value = "filePath", required = true) String path) {
        fastDFSClient.deleteFile(path);
        return ResultResponse.ok();
    }
    */
}
