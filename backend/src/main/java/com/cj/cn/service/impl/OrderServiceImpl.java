package com.cj.cn.service.impl;

import com.cj.cn.common.Const;
import com.cj.cn.mapper.*;
import com.cj.cn.pojo.*;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import com.cj.cn.util.BigDecimalUtil;
import com.cj.cn.util.LocalDateTimeUtil;
import com.cj.cn.vo.OrderItemVO;
import com.cj.cn.vo.OrderProductVO;
import com.cj.cn.vo.OrderVO;
import com.cj.cn.vo.ShippingVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResultResponse createOrder(Integer userId, Integer shippingId) {
        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        //查出所有用户勾选的购物车中的产品
        criteria.andEqualTo("checked", 1).andEqualTo("userId", userId);
        List<Cart> cartList = cartMapper.selectByExample(example);
        ResultResponse response = this.getCartOrderItem(cartList);
        if (!response.isSuccess()) {
            return response;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);    //计算订单的总价

        //持久化生成订单到数据库
        Order order = new Order();
        order.setOrderNo(this.generateOrderNo()).setUserId(userId).setShippingId(shippingId)
                .setPayment(payment).setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode())
                .setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        int rowCount = orderMapper.insert(order);
        if (rowCount == 0) {
            return ResultResponse.error("生成订单错误");
        }

        if (CollectionUtils.isEmpty(orderItemList)) {
            return ResultResponse.ok("购物车为空");
        }
        orderItemMapper.batchInsert(orderItemList);

        //订单已生成, 需要减少库存
        this.reduceProductStock(orderItemList);

        //清空购物车
        this.cleanCart(cartList);

        //返回给前端数据
        OrderVO orderVO = this.copyOrderVOByOrderAndOrderItemList(order, orderItemList);
        return ResultResponse.ok(orderVO);
    }

    private OrderVO copyOrderVOByOrderAndOrderItemList(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo()).setPayment(order.getPayment()).
                setPaymentType(order.getPaymentType()).setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue())
                .setPostage(order.getPostage())
                .setStatus(order.getStatus()).setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue())
                .setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVO(this.copyShippingVOByShipping(shipping));
        }
        orderVO.setPaymentTime(LocalDateTimeUtil.dateToStr(order.getPaymentTime()))
                .setSendTime(LocalDateTimeUtil.dateToStr(order.getSendTime()))
                .setEndTime(LocalDateTimeUtil.dateToStr(order.getEndTime()))
                .setCreateTime(LocalDateTimeUtil.dateToStr(order.getCreateTime()))
                .setCloseTime(LocalDateTimeUtil.dateToStr(order.getCloseTime()));

        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = this.copyOrderItemVOByOrderItem(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        return orderVO;
    }

    private OrderItemVO copyOrderItemVOByOrderItem(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo()).setProductId(orderItem.getProductId())
                .setProductName(orderItem.getProductName()).setProductImage(orderItem.getProductImage())
                .setCurrentUnitPrice(orderItem.getCurrentUnitPrice()).setQuantity(orderItem.getQuantity())
                .setTotalPrice(orderItem.getTotalPrice()).setCreateTime(LocalDateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private ShippingVO copyShippingVOByShipping(Shipping shipping) {
        ShippingVO shippingVO = new ShippingVO();
        shippingVO.setReceiverName(shipping.getReceiverName()).setReceiverPhone(shipping.getReceiverPhone())
                .setReceiverMobile(shipping.getReceiverMobile()).setReceiverProvince(shipping.getReceiverProvince())
                .setReceiverCity(shipping.getReceiverCity()).setReceiverDistrict(shipping.getReceiverDistrict())
                .setReceiverAddress(shipping.getReceiverAddress()).setReceiverZip(shipping.getReceiverZip());
        return shippingVO;
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ResultResponse getCartOrderItem(List<Cart> cartList) {
        if (CollectionUtils.isEmpty(cartList)) {
            return ResultResponse.error("购物车为空");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product != null) {
                //校验产品的状态
                if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
                    return ResultResponse.error("产品" + product.getName() + "不是在线售卖状态");
                }
                //校验库存
                if (product.getStock() < cart.getQuantity()) {
                    return ResultResponse.error("产品" + product.getName() + "库存不足");
                }
                orderItem.setUserId(cart.getId()).setProductId(product.getId())
                        .setProductName(product.getName()).setProductImage(product.getMainImage())
                        .setCurrentUnitPrice(product.getPrice()).setQuantity(cart.getQuantity())
                        .setTotalPrice(BigDecimalUtil.add(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
                orderItemList.add(orderItem);
            }
        }
        return ResultResponse.ok(orderItemList);
    }

    @Override
    public ResultResponse cancel(Integer userId, Long orderNo) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(example);
        if (order == null) {
            return ResultResponse.error("该用户此订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ResultResponse.error("已付款, 无法取消订单");
        }
        Order updateOrder = new Order();
        order.setId(order.getId()).setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (rowCount > 0) {
            return ResultResponse.ok();
        } else {
            return ResultResponse.error("取消订单失败");
        }
    }

    @Override
    public ResultResponse getOrderCartProduct(Integer userId) {
        OrderProductVO orderProductVO = new OrderProductVO();
        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("checked", 1).andEqualTo("userId", userId);
        List<Cart> cartList = cartMapper.selectByExample(example);
        ResultResponse response = this.getCartOrderItem(cartList);
        if (!response.isSuccess()) {
            return response;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) response.getData();
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(this.copyOrderItemVOByOrderItem(orderItem));
        }
        orderProductVO.setProductTotalPrice(payment);
        orderProductVO.setOrderItemVOList(orderItemVOList);
//        orderProductVO.setImageHost()
        return ResultResponse.ok(orderProductVO);
    }

    @Override
    public ResultResponse getOrderDetail(Integer userId, Long orderNo) {
        Example exampleForOrder = new Example(Order.class);
        Example.Criteria criteriaForOrder = exampleForOrder.createCriteria();
        criteriaForOrder.andEqualTo("orderNo", orderNo).andEqualTo("userId", userId);
        Order order = orderMapper.selectOneByExample(exampleForOrder);
        if (order == null) {
            return ResultResponse.error("找不到该订单");
        }

        Example exampleForOrderItem = new Example(Order.class);
        Example.Criteria criteriaForOrderItem = exampleForOrder.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo).andEqualTo("userId", userId);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.copyOrderVOByOrderAndOrderItemList(order, orderItemList);
        return ResultResponse.ok(orderVO);
    }

    @Override
    public ResultResponse getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        example.orderBy("createTime").desc();
        criteria.andEqualTo("userId", userId);
        List<Order> orderList = orderMapper.selectByExample(example);
        List<OrderVO> orderVOList = this.copyOrderVOListByOrderList(userId, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ResultResponse.ok(pageInfo);
    }

    private List<OrderVO> copyOrderVOListByOrderList(Integer userId, List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = new ArrayList<>();
            Example example = new Example(OrderItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderNo", order.getOrderNo());
            if (userId == null) {   //管理员查询的时候不传userId
                orderItemList = orderItemMapper.selectByExample(example);
            } else {
                criteria.andEqualTo("userId", userId);
                orderItemList = orderItemMapper.selectByExample(example);
            }
            OrderVO orderVO = this.copyOrderVOByOrderAndOrderItemList(order, orderItemList);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public ResultResponse getManageOrderList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(Order.class);
        example.orderBy("createTime").desc();
        List<Order> orderList = orderMapper.selectByExample(example);
        List<OrderVO> orderVOList = this.copyOrderVOListByOrderList(null, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ResultResponse.ok(pageInfo);
    }

    @Override
    public ResultResponse getManageDetail(Long orderNo) {
        Example exampleForOrder = new Example(Order.class);
        Example.Criteria criteriaForOrder = exampleForOrder.createCriteria();
        criteriaForOrder.andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(exampleForOrder);
        if (order == null) {
            return ResultResponse.error("找不到该订单");
        }

        Example exampleForOrderItem = new Example(Order.class);
        Example.Criteria criteriaForOrderItem = exampleForOrder.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.copyOrderVOByOrderAndOrderItemList(order, orderItemList);
        return ResultResponse.ok(orderVO);
    }

    @Override
    public ResultResponse getManageSearch(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Example exampleForOrder = new Example(Order.class);
        Example.Criteria criteriaForOrder = exampleForOrder.createCriteria();
        criteriaForOrder.andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(exampleForOrder);
        if (order == null) {
            return ResultResponse.error("找不到该订单");
        }

        Example exampleForOrderItem = new Example(Order.class);
        Example.Criteria criteriaForOrderItem = exampleForOrder.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.copyOrderVOByOrderAndOrderItemList(order, orderItemList);

        PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
        pageResult.setList(Lists.newArrayList(orderVO));
        return ResultResponse.ok(pageResult);
    }

    @Override
    public ResultResponse manageSendGoods(Long orderNo) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(example);
        if (order == null) {
            return ResultResponse.error("订单不存在");
        }

        if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(LocalDateTime.now());
            orderMapper.updateByPrimaryKeySelective(order);
            return ResultResponse.ok("发货成功");
        } else {
            return ResultResponse.error("当前订单状态不是已付款状态, 无法发货");
        }
    }
}
