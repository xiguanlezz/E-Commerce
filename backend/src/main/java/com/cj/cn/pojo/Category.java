package com.cj.cn.pojo;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ApiModel("品类实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_category")
public class Category {
    @ApiModelProperty(value = "类别Id", example = "100020")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty(value = "父类别id当id=0时说明是根节点, 一级类别", example = "0")
    @Column(name = "parent_id")
    private Integer parentId;

    @ApiModelProperty("类别名称")
    private String name;

    @ApiModelProperty("类别状态: 1-正常, 2-已废弃")
    private Boolean status;

    @ApiModelProperty(value = "排序编号, 同类展示顺序, 数值相等则自然排序", example = "1")
    @Column(name = "sort_order")
    private Integer sortOrder;

    @ApiModelProperty("品类创建时间")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("品类最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}