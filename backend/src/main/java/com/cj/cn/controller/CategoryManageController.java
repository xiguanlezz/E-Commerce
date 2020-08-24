package com.cj.cn.controller;

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
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类请求
     *
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    public ResultResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
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

    /**
     * 修改品类名字请求
     *
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    public ResultResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    /**
     * 查询一层子节点的category信息
     *
     * @param session
     * @param parentId
     * @return
     */
    @RequestMapping("get_category.do")
    public ResultResponse getParallelChildrenCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询子节点的category信息, 并且不递归, 保持平级
            return iCategoryService.getParallelChildrenCategory(parentId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    public ResultResponse getDeepChildrenCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //递归查询该节点和所有子节点的id
            return iCategoryService.getDeepChildrenCategory(parentId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }
}
