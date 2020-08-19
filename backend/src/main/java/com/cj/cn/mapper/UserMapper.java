package com.cj.cn.mapper;

import com.cj.cn.entity.User;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {
    Integer checkUsername(String username);

    Integer checkEmail(String email);
}