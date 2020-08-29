package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.mapper.CartMapper;
import com.cj.cn.pojo.Cart;
import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.ICartService;
import com.cj.cn.util.BigDecimalUtil;
import com.cj.cn.vo.CartProductVO;
import com.cj.cn.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Override
    public ResultResponse list(Integer userId) {
        return ResultResponse.ok(this.getCartVOLimit(userId));
    }

    @Override
    public ResultResponse add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("productId", productId);
        Cart cart = cartMapper.selectOneByExample(example);
        if (cart == null) {      //需要在表中插入一个新的购物车记录
            Cart cartItem = new Cart();
            cartItem.setUserId(userId).setProductId(productId).setQuantity(count).setChecked(Const.Cart.CHECKED)
                    .setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
            cartMapper.insert(cartItem);
        } else {    //仅改变数量
            int newCount = cart.getQuantity() + count;
            cart.setQuantity(newCount);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ResultResponse update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("productId", productId);
        Cart cart = cartMapper.selectOneByExample(example);
        if (cart == null) {
            return ResultResponse.error("更新购物车中产品数量失败");
        }
        cart.setQuantity(count).setUpdateTime(LocalDateTime.now());
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    public ResultResponse deleteProduct(Integer userId, String productIds) {
//        List<String> productList = Splitter.on(",").splitToList(productIds);
        String[] strings = productIds.split(",");
        List<String> productIdList = new ArrayList<>();
        for (String s : strings) {
            productIdList.add(s);   //Collections.addAll(productIdList,strings);
        }
        cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
        return this.list(userId);
    }

    @Override
    public ResultResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUnCheckedProduct(userId, productId, checked);
        return this.list(userId);
    }

    @Override
    public ResultResponse getCartProductCount(Integer userId) {
        if (userId == null) {
            return ResultResponse.ok(0);
        }
        return ResultResponse.ok(cartMapper.selectCartProductCount(userId));
    }

    private CartVO getCartVOLimit(Integer userId) {
        BigDecimal cartTotalPrice = new BigDecimal("0");
        List<CartProductVO> cartProductVOList = cartMapper.selectCartProductVOListByUserId(userId);
        CartVO cartVO = null;
        if (CollectionUtils.isNotEmpty(cartProductVOList)) {    //mybatis查询出来的List可能不是null但size可能是0
            for (CartProductVO cartProductVO : cartProductVOList) {
                int stockCount = cartProductVO.getProductStock();
                //添加到购物车的时候还不需要减产品库存
                if (stockCount >= cartProductVO.getQuantity()) {
                    cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                } else {    //库存不足
                    int buyLimitCount = stockCount;
                    cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    //更新购物车的产品数量为最大购买数量(库存量)
                    Cart cartForQuantity = new Cart().setId(cartProductVO.getId()).setQuantity(buyLimitCount);
                    cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    cartProductVO.setQuantity(buyLimitCount);   //更新返回给前端的数量
                }
                //计算购物车中某件产品的总价
                cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(cartProductVO.getProductPrice().doubleValue(), cartProductVO.getQuantity()));
                if (cartProductVO.getProductChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选, 增加产品总价到购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVO.getProductTotalPrice().doubleValue());
                }
            }
            cartVO = new CartVO();
            cartVO.setCartTotalPrice(cartTotalPrice);
            cartVO.setCartProductVoList(cartProductVOList);
            cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        }
        return cartVO;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
