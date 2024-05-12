package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.dto.UserDTO;
import com.example.pojo.entity.User;
import com.example.utils.Result;

/**
 * @author 胡金勇
 * @description 针对表【news_user】的数据库操作Service
 * @createDate 2024-03-20 20:45:23
 */
public interface UserService extends IService<User> {

    /**
     * 登录业务
     */
    Result login(User user);

    /**
     * 根据token获取用户数据
     */
    Result getUserInfo(String token);

    /**
     * 检查注册用户名是否可用
     */
    Result checkUserName(String username);

    /**
     * 用户注册
     */
    Result register(UserDTO userDTO);

}
