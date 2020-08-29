package com.cj.cn.pojo;

import java.time.LocalDateTime;
import javax.persistence.*;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@ApiModel("用户实体类")
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Table(name = "mmall_user")   //通用mapper默认查询的表名是实体类类名首字母小写
public class User {
    @ApiModelProperty(value = "用户id", example = "13")    //使用example属性解决swagger的警告问题
    @Id     //如果没有使用这个注解则默认将实体类中的所有字段当做联合主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //返回自增的主键
    private Integer id;

    @ApiModelProperty("用户名(唯一)")
    private String username;

    @ApiModelProperty("用户密码, MD5加密")
    private String password;

    @ApiModelProperty("用户邮箱(唯一)")
    private String email;

    @ApiModelProperty("用户手机号")
    private String phone;

    @ApiModelProperty("用户找回密码的问题")
    private String question;

    @ApiModelProperty("用户找回密码问题的答案")
    private String answer;

    @ApiModelProperty(value = "用户角色标记位, 0表示管理员, 1表示普通用户", example = "1")
    private Integer role;

    @ApiModelProperty("用户创建时间")
    @Column(name = "create_time")   //指定数据库表中对应的字段, 默认是去掉驼峰
    private LocalDateTime createTime;

    @ApiModelProperty("用户最后更新时间")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient  //表示该字段不是要映射到数据库表中的字段
    private String test;
}