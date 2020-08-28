package com.cj.cn.controller.api;

import com.cj.cn.common.Const;
import com.cj.cn.pojo.Shipping;
import com.cj.cn.pojo.User;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShoppingService;

    @PostMapping("add.do")
    public ResultResponse add(Shipping shipping, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.add(user.getId(), shipping);
    }

    @DeleteMapping("del.do")
    public ResultResponse del(Integer shippingId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.del(user.getId(), shippingId);
    }

    @PutMapping("update.do")
    public ResultResponse update(Shipping shipping, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.update(user.getId(), shipping);
    }

    @GetMapping("select.do")
    public ResultResponse select(Integer shippingId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.select(user.getId(), shippingId);
    }

    @GetMapping("list.do")
    public ResultResponse list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResultResponse.error("用户未登录");
        }
        return iShoppingService.list(user.getId(), pageNum, pageSize);
    }

}
