package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ApiModel("购物车实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_cart")
public class Cart {
    @ApiModelProperty(value = "购物车id", example = "126")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "用户id", example = "13")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "商品id", example = "26")
    @Column(name = "product_id")
    private Integer productId;

    @ApiModelProperty(value = "数量", example = "1")
    private Integer quantity;

    @ApiModelProperty(value = "是否选择, 1=已勾选, 0=未勾选", example = "1")
    private Integer checked;

    @ApiModelProperty("购物车创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("购物车最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}