package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "订单模块")
@RestController
@RequestMapping("/order/")
public class OrderController {
    @Autowired
    private IOrderService iOrderService;

    @ApiOperation(value = "用户下订单的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;生成订单表、订单明细表, 清空购物车并修改产品库存")
    @ApiImplicitParam(name = "shippingId", value = "地址id")
    @PostMapping("create.do")
    public ResultResponse create(@RequestParam("shippingId") Integer shippingId,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    @ApiOperation(value = "用户取消订单的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;修改订单状态为已取消")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @DeleteMapping("cancel.do")
    public ResultResponse cancel(@RequestParam("orderNo") Long orderNo,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    @ApiOperation(value = "查看勾选产品详细信息的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;获取用户勾选的购物车中产品的详细信息")
    @GetMapping("get_order_cart_product.do")
    public ResultResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @ApiOperation(value = "查看订单详情的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;通过订单号查看订单的详细信息")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @GetMapping("detail.do")
    public ResultResponse detail(@RequestParam("orderNo") Long orderNo,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @ApiOperation(value = "个人中心查看我的订单接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;个人中心查看我的订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "页容量"),
    })
    @GetMapping("list.do")
    public ResultResponse detail(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }
}
