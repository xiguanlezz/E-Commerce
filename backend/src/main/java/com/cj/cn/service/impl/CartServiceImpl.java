package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.mapper.CartMapper;
import com.cj.cn.pojo.Cart;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICartService;
import com.cj.cn.util.BigDecimalUtil;
import com.cj.cn.vo.CartProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Override
    public ResultResponse list(Integer userId) {
        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<CartProductVO> cartProductVOList = cartMapper.selectCartProductVOListByUserId(userId);
        for (CartProductVO cartProductVO : cartProductVOList) {
            int stockCount = cartProductVO.getProductStock();
            //添加到购物车的时候还不需要减产品库存
            if (stockCount >= cartProductVO.getQuantity()) {
                cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
            } else {    //库存不足
                int buyLimitCount = stockCount;
                cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                //更新购物车的产品数量为最大购买数量(库存量)
                cartMapper.updateByPrimaryKeySelective(new Cart().setId(cartProductVO.getId()).setQuantity(buyLimitCount));
                cartProductVO.setQuantity(buyLimitCount);   //更新返回给前端的数量
            }
            //计算总价
            cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(cartProductVO.getProductPrice().doubleValue(), cartProductVO.getQuantity()));
        }
        return ResultResponse.ok(cartProductVOList);
    }

//    private CartProductVO copyCartProductVoByCart(Cart cart) {
//
//    }
}
