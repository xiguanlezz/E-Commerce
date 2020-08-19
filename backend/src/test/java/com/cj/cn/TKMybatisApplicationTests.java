package com.cj.cn;

import com.cj.cn.entity.User;
import com.cj.cn.mapper.UserMapper;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = StartApplication.class)
@RunWith(SpringRunner.class)
public class TKMybatisApplicationTests {

    @Autowired
    private UserMapper userMapper;


    public TKMybatisApplicationTests() {
    }

    /**
     * 根据对象属性查询
     */
    @Test
    public void testSelectOne() {
        User user = new User();
        user.setPassword("123456");
        User u = userMapper.selectOne(user);
        System.out.println(u);
    }

    /**
     * 根据主键查询
     */
    @Test
    public void testSelectByPrimaryKey() {
        Integer employeeId = 1;
        User u = userMapper.selectByPrimaryKey(employeeId);
        System.out.println(u);
    }

    /**
     * 根据主键判断数据记录是否存在
     */
    @Test
    public void testExistsWithPrimaryKey() {
        Integer userId = 13;
        boolean b = userMapper.existsWithPrimaryKey(userId);
        System.out.println(b);
    }

    /**
     * 全插入
     */
    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("xxyyx222").setPassword("111").setRole(0).setCreateTime(new Date()).setUpdateTime(new Date());
        userMapper.insert(user);
        System.out.println(user.getId());
    }

    /**
     * 部分插入
     */
    @Test
    public void testInsertSelective() {
        User user = new User();
        user.setUsername("xxyyzz").setPassword("111").setRole(0).setCreateTime(new Date()).setUpdateTime(new Date());
        userMapper.insertSelective(user);       //为null的属性不加入sql语句
        System.out.println(user.getId());
    }

    /**
     * 根据主键部分更新
     */
    @Test
    public void testUpdateByPrimaryKeySelective() {
        User user = new User();
        user.setUsername("xxx").setRole(0).setPassword("012").setId(22);
        userMapper.updateByPrimaryKeySelective(user);       //为null的属性不执行更新操作
    }

    /**
     * 根据主键删除
     */
    @Test
    public void testDeleteByPrimaryKey() {
        Integer userId = 25;
        userMapper.deleteByPrimaryKey(userId);
    }
}
