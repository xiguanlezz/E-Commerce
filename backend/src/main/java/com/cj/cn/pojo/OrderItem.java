package com.cj.cn.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Api(tags = "订单子表明细实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_order_item")
public class OrderItem {
    @ApiModelProperty(value = "订单子表id", example = "113")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "用户id", example = "1")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "订单号", example = "1491753014256")
    @Column(name = "order_no")
    private Long orderNo;

    @ApiModelProperty(value = "商品id", example = "26")
    @Column(name = "product_id")
    private Integer productId;

    @ApiModelProperty("商品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty("商品图片地址")
    @Column(name = "product_image")
    private String productImage;

    @ApiModelProperty(value = "生成订单时的商品单价, 单位是元, 保留两位小数", example = "6999.00")
    @Column(name = "current_unit_price")
    private BigDecimal currentUnitPrice;

    @ApiModelProperty(value = "商品数量", example = "2")
    private Integer quantity;

    @ApiModelProperty(value = "商品总价, 单位是元, 保留两位小数", example = "13998.00")
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ApiModelProperty("交易创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("交易更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}