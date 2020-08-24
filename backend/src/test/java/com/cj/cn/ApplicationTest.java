package com.cj.cn;

import com.cj.cn.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = StartApplication.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        Integer i = userMapper.checkUsername("admin");
        System.out.println(i);
    }
}
