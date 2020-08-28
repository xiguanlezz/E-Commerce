package com.cj.cn.controller;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICategoryService;
import com.cj.cn.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Api(tags = "后台品类模块")
@RestController
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @ApiOperation(value = "添加品类接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台添加一个品类的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "品类的名字"),
            @ApiImplicitParam(name = "parentId", value = "品类的父id")
    })
    @PostMapping("add_category.do")
    public ResultResponse addCategory(@RequestParam("categoryName") String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId,
                                      HttpSession session) {
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

    @ApiOperation(value = "修改品类名字接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台修改品类名字的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "品类的id"),
            @ApiImplicitParam(name = "categoryName", value = "品类的名字")
    })
    @PostMapping("set_category_name.do")
    public ResultResponse setCategoryName(@RequestParam("categoryId") Integer categoryId,
                                          @RequestParam("categoryName") String categoryName,
                                          HttpSession session) {
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

    @ApiOperation(value = "查询该品类下一层子品类的信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台根据传入的品类id去查询下一层子节点中品类信息")
    @ApiImplicitParam(name = "parentId", value = "品类id")
    @PostMapping("get_category.do")
    public ResultResponse getParallelChildrenCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer categoryId,
                                                      HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询子节点的category信息, 并且不递归, 保持平级
            return iCategoryService.getParallelChildrenCategory(categoryId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }

    @ApiOperation(value = "递归查询本品类和该品类下所有子节点的id的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;后台根据传入的品类id去递归查询本品类和该品类下所有子节点的id")
    @ApiImplicitParam(name = "parentId", value = "品类id")
    @PostMapping("get_deep_category.do")
    public ResultResponse getDeepChildrenCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer categoryId,
                                                  HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //递归查询该节点和所有子节点的id
            return iCategoryService.getDeepChildrenCategory(categoryId);
        } else {
            return ResultResponse.error("无权限操作, 需要管理员权限");
        }
    }
}
