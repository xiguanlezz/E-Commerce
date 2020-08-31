package com.cj.cn.controller.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.cj.cn.common.Const;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "订单模块")
@RestController
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @ApiOperation(value = "用户付款的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;用户使用支付宝进行付款")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @PostMapping("pay.do")
    public ResultResponse pay(@RequestParam("orderNo") Long orderNo,
                              HttpServletRequest request,
                              HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");   //获取根目录
        return iOrderService.pay(user.getId(), orderNo, path);
    }

    @ApiOperation(value = "支付宝回调的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;支付宝回调")
    @PostMapping("alipay_callback.do")
    public Object payCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();     //支付宝回调回来的参数都在请求里面
        parameterMap.forEach((key, value) -> {
            String valueStr = "";
            for (int i = 0; i < value.length; i++) {
                valueStr = (i == value.length - 1 ? valueStr + value[i] : valueStr + value[i] + ",");
            }
            params.put(key, valueStr);  //手动拼接参数
        });
        logger.info("支付宝回调, sign: {}, trade_status: {}, 参数: {}", params.get("sing"), params.get("trade_status"), params.toString());

        params.remove("sign_type");  //必须要移除, 源码里面只移除了sign
        try {
            //需要校验回调通知的发起方
            boolean b = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!b) {
                return ResultResponse.error("请勿恶意请求, 在请求的话我就要报警了");   //说明不是支付宝发出的回调通知
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝回调发生异常: ", e);
        }

        ResultResponse response = iOrderService.aliCallback(params);
        if (response.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @ApiOperation(value = "查看支付宝订单状态的接口", notes = "<span style='color:red;'>描述:</span>&nbsp;&nbsp;前端轮询, 查看支付宝订单状态")
    @ApiImplicitParam(name = "orderNo", value = "订单号")
    @PostMapping("query_order_pay_status.do")
    public ResultResponse queryOrderPayStatus(@RequestParam("orderNo") Long orderNo,
                                              HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        ResultResponse response = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (response.isSuccess()) {
            return ResultResponse.ok(true);
        }
        return ResultResponse.ok(false);
    }

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
    @PutMapping("cancel.do")
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
    public ResultResponse list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }
}
