package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "mmall_shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 收货姓名
     */
    @Column(name = "receiver_name")
    private String receiverName;

    /**
     * 收货固定电话
     */
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货移动电话
     */
    @Column(name = "receiver_mobile")
    private String receiverMobile;

    /**
     * 省份
     */
    @Column(name = "receiver_province")
    private String receiverProvince;

    /**
     * 城市
     */
    @Column(name = "receiver_city")
    private String receiverCity;

    /**
     * 区/县
     */
    @Column(name = "receiver_district")
    private String receiverDistrict;

    /**
     * 详细地址
     */
    @Column(name = "receiver_address")
    private String receiverAddress;

    /**
     * 邮编
     */
    @Column(name = "receiver_zip")
    private String receiverZip;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}