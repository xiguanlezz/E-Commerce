package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Api(tags = "支付信息实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_pay_info")
public class PayInfo {
    @ApiModelProperty(value = "支付信息的id", example = "53")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "用户id", example = "1")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "订单号", example = "1492090946105")
    @Column(name = "order_no")
    private Long orderNo;

    @ApiModelProperty(value = "支付平台: 1-支付宝, 2-微信", example = "1")
    @Column(name = "pay_platform")
    private Integer payPlatform;

    @ApiModelProperty("支付宝支付流水号")
    @Column(name = "platform_number")
    private String platformNumber;

    @ApiModelProperty("支付宝支付状态")
    @Column(name = "platform_status")
    private String platformStatus;

    @ApiModelProperty("交易创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("交易最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}