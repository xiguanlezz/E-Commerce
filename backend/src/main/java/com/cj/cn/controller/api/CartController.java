package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.Shipping;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICartService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "购物车模块")
@RestController
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @GetMapping("add.do")
    public ResultResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }
}
