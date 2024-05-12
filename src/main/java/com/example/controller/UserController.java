package com.example.controller;

import com.example.pojo.dto.UserDTO;
import com.example.pojo.entity.User;
import com.example.service.UserService;
import com.example.utils.JwtHelper;
import com.example.utils.Result;
import com.example.utils.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@Slf4j
// @CrossOrigin // 前端已经解决跨域问题
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("login")
    public Result login(@RequestBody User user) {

        log.info("用户登录信息：{}", user);

        Result result = userService.login(user);
        return result;
    }

    /**
     * 获取用户信息(登陆成功后同时执行)
     *
     * @param token
     * @return
     */
    @GetMapping("getUserInfo")
    public Result getUserInfo(@RequestHeader String token) {

        log.info("获取用户信息(登陆成功后同时执行)...");

        Result result = userService.getUserInfo(token);
        return result;
    }

    @PostMapping("checkUserName")
    public Result checkUserName(String username) {

        log.info("检查注册的用户姓名");

        Result result = userService.checkUserName(username);
        return result;
    }

    @PostMapping("regist")
    public Result regist(@RequestBody UserDTO userDTO) {

        log.info("用户注册信息：{}", userDTO);

        Result result = userService.register(userDTO);
        return result;
    }

    /**
     * 登录校验接口
     */
    @GetMapping("checkLogin")
    public Result checkLogin(@RequestHeader String token) {
        if (jwtHelper.isExpiration(token)) {
            // token已经过期
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        return Result.success(null);
    }

}
