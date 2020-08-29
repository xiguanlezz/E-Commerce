package com.cj.cn.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Api(tags = "订单实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_order")
public class Order {
    @ApiModelProperty(value = "订单id", example = "103")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "订单号", example = "1491753014256")
    @Column(name = "order_no")
    private Long orderNo;

    @ApiModelProperty(value = "用户id", example = "1")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "地址id", example = "25")
    @Column(name = "shipping_id")
    private Integer shippingId;

    @ApiModelProperty(value = "实际付款金额, 单位是元, 保留两位小数", example = "13998.00")
    private BigDecimal payment;

    @ApiModelProperty(value = "支付类型, 1-在线支付", example = "1")
    @Column(name = "payment_type")
    private Integer paymentType;

    @ApiModelProperty(value = "运费, 单位是元", example = "0")
    private Integer postage;

    @ApiModelProperty(value = "订单状态: 0-已取消, 10-未付款, 20-已付款, 40-已发货, 50-交易成功, 60-交易关闭", example = "10")
    private Integer status;

    @ApiModelProperty("支付时间")
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @ApiModelProperty("发货时间")
    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @ApiModelProperty("交易完成时间")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ApiModelProperty("交易关闭时间")
    @Column(name = "close_time")
    private LocalDateTime closeTime;

    @ApiModelProperty("交易创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("交易更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}