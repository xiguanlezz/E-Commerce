package com.cj.cn.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.cj.cn.common.Const;
import com.cj.cn.mapper.*;
import com.cj.cn.pojo.*;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IOrderService;
import com.cj.cn.util.BigDecimalUtil;
import com.cj.cn.util.FastDFSClientUtil;
import com.cj.cn.util.LocalDateTimeUtil;
import com.cj.cn.util.PropertiesUtil;
import com.cj.cn.vo.OrderItemVO;
import com.cj.cn.vo.OrderProductVO;
import com.cj.cn.vo.OrderVO;
import com.cj.cn.vo.ShippingVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import net.bytebuddy.asm.Advice;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Autowired
    private FastDFSClientUtil fastDFSClient;
    @Autowired
    private PayInfoMapper payInfoMapper;
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
    public ResultResponse pay(Integer userId, Long orderNo, String path) {
        Map<String, String> resultMap = new HashMap<>();
        Example exampleForOrder = new Example(Order.class);
        Example.Criteria criteriaForOrder = exampleForOrder.createCriteria();
        criteriaForOrder.andEqualTo("userId", userId).andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(exampleForOrder);
        if (order == null) {
            return ResultResponse.error("用户没有该订单");
        }

        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderNo.toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = new StringBuilder().append("mmall扫码支付, 订单号: ").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append(outTradeNo).append("购买商品共").append(totalAmount).toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail

        Example exampleForOrderItem = new Example(OrderItem.class);
        Example.Criteria criteriaForOrderItem = exampleForOrderItem.createCriteria();
        criteriaForOrderItem.andEqualTo("userId", userId).andEqualTo("orderNo", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getOrderNo().toString(), //商品id
                    orderItem.getProductName(), //商品名称
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100)).longValue(), //商品单价(单位为分)
                    orderItem.getQuantity());   //商品数量
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path, qrFileName);   //先将二维码保存到本地, 再上传到FastDFS
                try {
                    String qrUrl = PropertiesUtil.getProperty("fastdfs.address") + "/" + fastDFSClient.uploadFile(targetFile);
                    resultMap.put("qrUrl", qrUrl);
                } catch (Exception e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath: " + qrPath);
                return ResultResponse.ok(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ResultResponse.error("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ResultResponse.error("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ResultResponse.error("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    @Override
    public ResultResponse aliCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));  //商户订单号
        String tradeNo = params.get("trade_no");    //支付宝交易号
        String tradeStatus = params.get("trade_status");    //订单状态
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(example);
        if (order == null) {
            return ResultResponse.error("不是mmall订单的回调, 回调忽略");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ResultResponse.ok("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(LocalDateTimeUtil.strToDate(params.get("gmt_payment")));   //交易付款时间
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId()).setOrderNo(order.getOrderNo())
                .setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode())
                .setPlatformNumber(tradeNo).setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ResultResponse.ok();
    }

    @Override
    public ResultResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Example exampleForOrder = new Example(Order.class);
        Example.Criteria criteriaForOrder = exampleForOrder.createCriteria();
        criteriaForOrder.andEqualTo("userId", userId).andEqualTo("orderNo", orderNo);
        Order order = orderMapper.selectOneByExample(exampleForOrder);
        if (order == null) {
            return ResultResponse.error("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ResultResponse.ok("用户已付款");
        }
        return ResultResponse.error("当前用户还未付款");
    }

    @Override
    public ResultResponse createOrder(Integer userId, Integer shippingId) {
        Example example = new Example(Cart.class);
        Example.Criteria criteria = example.createCriteria();
        //查出用户勾选的所有购物车记录
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
                .setStatus(Const.OrderStatusEnum.NO_PAY.getCode())
                .setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
        int rowCount = orderMapper.insert(order);
        if (rowCount == 0) {
            return ResultResponse.error("生成订单错误");
        }

        if (CollectionUtils.isEmpty(orderItemList)) {
            return ResultResponse.ok("购物车为空");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());   //更新订单子表的订单号
        }
        orderItemMapper.batchInsert(orderItemList);     //持久化到数据库

        //订单已生成, 需要减少库存
        this.reduceProductStock(orderItemList);

        //清空购物车
        this.cleanCart(cartList);

        //返回给前端数据
        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
        return ResultResponse.ok(orderVO);
    }

    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo()).setPayment(order.getPayment()).
                setPaymentType(order.getPaymentType()).setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue())
                .setPostage(order.getPostage())
                .setStatus(order.getStatus()).setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue())
                .setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVO.setReceiverName(shipping.getReceiverName());
            orderVO.setShippingVO(this.assembleShippingVO(shipping));
        }
        orderVO.setPaymentTime(LocalDateTimeUtil.dateToStr(order.getPaymentTime()))
                .setSendTime(LocalDateTimeUtil.dateToStr(order.getSendTime()))
                .setEndTime(LocalDateTimeUtil.dateToStr(order.getEndTime()))
                .setCloseTime(LocalDateTimeUtil.dateToStr(order.getCloseTime()))
                .setCreateTime(LocalDateTimeUtil.dateToStr(order.getCreateTime()));

        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = this.assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        return orderVO;
    }

    private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        if (orderItem.getOrderNo() != null) {
            orderItemVO.setOrderNo(orderItem.getOrderNo());
        }
        orderItemVO.setProductId(orderItem.getProductId())
                .setProductName(orderItem.getProductName()).setProductImage(orderItem.getProductImage())
                .setCurrentUnitPrice(orderItem.getCurrentUnitPrice()).setQuantity(orderItem.getQuantity())
                .setTotalPrice(orderItem.getTotalPrice()).setCreateTime(LocalDateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    private ShippingVO assembleShippingVO(Shipping shipping) {
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
                orderItem.setUserId(cart.getUserId()).setProductId(product.getId())
                        .setProductName(product.getName()).setProductImage(product.getMainImage())
                        .setCurrentUnitPrice(product.getPrice()).setQuantity(cart.getQuantity())
                        .setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
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
        updateOrder.setId(order.getId()).setStatus(Const.OrderStatusEnum.CANCELED.getCode()).setUpdateTime(LocalDateTime.now());
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
            orderItemVOList.add(this.assembleOrderItemVO(orderItem));
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

        Example exampleForOrderItem = new Example(OrderItem.class);
        Example.Criteria criteriaForOrderItem = exampleForOrderItem.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo).andEqualTo("userId", userId);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
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
        List<OrderVO> orderVOList = this.assembleOrderVOList(userId, orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return ResultResponse.ok(pageInfo);
    }

    private List<OrderVO> assembleOrderVOList(Integer userId, List<Order> orderList) {
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
            OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
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
        List<OrderVO> orderVOList = this.assembleOrderVOList(null, orderList);
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

        Example exampleForOrderItem = new Example(OrderItem.class);
        Example.Criteria criteriaForOrderItem = exampleForOrder.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
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

        Example exampleForOrderItem = new Example(OrderItem.class);
        Example.Criteria criteriaForOrderItem = exampleForOrder.createCriteria();
        criteriaForOrderItem.andEqualTo("orderNo", orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(exampleForOrderItem);
        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);

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
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode()).setSendTime(LocalDateTime.now());
            orderMapper.updateByPrimaryKeySelective(order);
            return ResultResponse.ok("发货成功");
        } else {
            return ResultResponse.error("当前订单状态不是已付款状态, 无法发货");
        }
    }
}
