package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Api(tags = "地址实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_shipping")
public class Shipping {
    @ApiModelProperty(value = "地址id", example = "4")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "用户id", example = "13")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty("收货姓名")
    @Column(name = "receiver_name")
    private String receiverName;

    @ApiModelProperty("收货固定电话")
    @Column(name = "receiver_phone")
    private String receiverPhone;

    @ApiModelProperty("收货移动电话")
    @Column(name = "receiver_mobile")
    private String receiverMobile;

    @ApiModelProperty("省份")
    @Column(name = "receiver_province")
    private String receiverProvince;

    @ApiModelProperty("城市")
    @Column(name = "receiver_city")
    private String receiverCity;

    @ApiModelProperty("区/县")
    @Column(name = "receiver_district")
    private String receiverDistrict;

    @ApiModelProperty("详细地址")
    @Column(name = "receiver_address")
    private String receiverAddress;

    @ApiModelProperty("邮编")
    @Column(name = "receiver_zip")
    private String receiverZip;

    @ApiModelProperty("地址创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("地址最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}