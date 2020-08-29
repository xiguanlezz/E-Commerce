package com.cj.cn.mapper;

import com.cj.cn.pojo.Shipping;
import tk.mybatis.mapper.common.Mapper;

public interface ShippingMapper extends Mapper<Shipping> {
    int updateByShipping(Shipping shipping);
}