package com.cj.cn.mapper;

import com.cj.cn.pojo.Cart;
import com.cj.cn.vo.CartProductVO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CartMapper extends Mapper<Cart> {
    List<CartProductVO> selectCartProductVOListByUserId(Integer userId);
}