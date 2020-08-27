package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_user")   //通用mapper默认查询的表名是实体类类名首字母小写
public class User {
    /**
     * 用户表id
     */
    @Id     //如果没有使用这个注解则默认将实体类中的所有字段当做联合主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码，MD5加密
     */
    private String password;

    private String email;

    private String phone;

    /**
     * 找回密码问题
     */
    private String question;

    /**
     * 找回密码答案
     */
    private String answer;

    /**
     * 角色0-管理员,1-普通用户
     */
    private Integer role;

    /**
     * 创建时间
     */
    @Column(name = "create_time")   //指定数据库表中对应的字段, 默认是去掉驼峰
    private LocalDateTime createTime;

    /**
     * 最后一次更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient  //表示该字段不是要映射到数据库表中的字段
    private String test;
}