package com.cj.cn.service.impl;

import com.cj.cn.mapper.ShippingMapper;
import com.cj.cn.pojo.Shipping;
import com.cj.cn.response.ResultResponse;
import com.cj.cn.service.IShoppingService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iShoppingService")
public class ShoppingServiceImpl implements IShoppingService {
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ResultResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map<String, Integer> result = new HashMap();
            result.put("shippingId", shipping.getId());
            return ResultResponse.ok("新增地址成功", result);
        } else {
            return ResultResponse.error("新增地址失败");
        }
    }

    @Override
    public ResultResponse del(Integer userId, Integer shippingId) {
        Example example = new Example(Shipping.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("shippingId", shippingId);
        int resultCount = shippingMapper.deleteByExample(example);
        if (resultCount > 0) {
            return ResultResponse.ok("删除地址成功");
        } else {
            return ResultResponse.ok("删除地址失败");
        }
    }

    @Override
    public ResultResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        Example example = new Example(Shipping.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        int rowCount = shippingMapper.updateByExampleSelective(shipping, example);
        if (rowCount > 0) {
            Map<String, Integer> result = new HashMap();
            result.put("shippingId", shipping.getId());
            return ResultResponse.ok("修改地址成功", result);
        } else {
            return ResultResponse.error("修改地址失败");
        }
    }

    @Override
    public ResultResponse select(Integer userId, Integer shippingId) {
        Example example = new Example(Shipping.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("shippingId", shippingId);
        Shipping shipping = shippingMapper.selectOneByExample(example);
        if (shipping == null) {
            return ResultResponse.error("无法查询到该地址");
        } else {
            return ResultResponse.ok(shipping);
        }
    }

    public ResultResponse list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);    //PageHelper分页使用先start
        Example example = new Example(Shipping.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        List<Shipping> shippingList = shippingMapper.selectByExample(example);

        PageInfo pageInfo = new PageInfo(shippingList);   //最后PageHelper再收尾
        return ResultResponse.ok(pageInfo);
    }
}
