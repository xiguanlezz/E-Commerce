package com.cj.cn.mapper;

import com.cj.cn.entity.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User> {
    Integer checkUsername(String username);

    Integer checkEmail(String email);

    String selectQuestionByUsername(String username);

    Integer checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    Integer updatePasswordByUsername(@Param("username") String username, @Param("password") String password);
}