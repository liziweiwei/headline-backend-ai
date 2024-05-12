package com.example.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
// 这个接口是一个标记接口（marker interface），它没有任何方法或字段，其主要作用是指示一个类可以被序列化
// 序列化是指将对象的状态信息转换为可以存储或传输的形式的过程
public class UserDTO implements Serializable {

    private String username;

    private String userPwd;

    private String nickName;
}
