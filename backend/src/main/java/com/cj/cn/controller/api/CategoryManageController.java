package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.entity.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICategoryService;
import com.cj.cn.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping
    public ResultResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parendId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @RequestMapping
    public ResultResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }
}
