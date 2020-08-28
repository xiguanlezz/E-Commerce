package com.cj.cn.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ApiModel("产品实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_product")
public class Product {
    @ApiModelProperty("商品id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty("分类id(对应mmall_category表的主键)")
    @Column(name = "category_id")
    private Integer categoryId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品副标题")
    private String subtitle;

    @ApiModelProperty("产品主图, url相对地址")
    @Column(name = "main_image")
    private String mainImage;

    @ApiModelProperty("图片地址, json格式,扩展用")
    @Column(name = "sub_images")
    private String subImages;

    @ApiModelProperty("商品详情")
    private String detail;

    @ApiModelProperty("价格,单位-元保留两位小数")
    private BigDecimal price;

    @ApiModelProperty("库存数量")
    private Integer stock;

    @ApiModelProperty("商品状态.1-在售 2-下架 3-删除")
    private Integer status;

    @ApiModelProperty("产品创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("产品最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}