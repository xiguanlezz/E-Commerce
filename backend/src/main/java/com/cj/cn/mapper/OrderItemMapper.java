package com.cj.cn.mapper;

import com.cj.cn.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderItemMapper extends Mapper<OrderItem> {
    void batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
}