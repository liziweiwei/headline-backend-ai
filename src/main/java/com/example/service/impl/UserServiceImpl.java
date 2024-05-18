package com.example.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.exception.AccountAlreadyExistsException;
import com.example.exception.AccountNotFoundException;
import com.example.exception.PasswordErrorException;
import com.example.mapper.UserMapper;
import com.example.pojo.dto.UserDTO;
import com.example.pojo.entity.User;
import com.example.service.UserService;
import com.example.utils.JwtHelper;
import com.example.utils.MD5Util;
import com.example.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtHelper jwtHelper;

    /**
     * 登录业务
     * 1.根据账号，查询用户对象  - loginUser
     * 2.如果用户对象为null，查询失败，账号错误！ 501
     * 3.对比密码;失败,返回503的错误
     * 4.根据用户id生成一个token, token -> result 返回
     *
     * @param user
     * @return
     */
    @Override
    public Result login(User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        User loginuser = userMapper.selectOne(lambdaQueryWrapper);

        if (loginuser == null) {
            // 账号不存在
            // return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        // 对比密码
        if (StringUtils.isEmpty(user.getUserPwd()) ||
                !MD5Util.encrypt(user.getUserPwd()).equals(loginuser.getUserPwd())) {
            // 密码错误
            // return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 登录成功,根据用户id生成token
        String token = jwtHelper.createToken(Long.valueOf(loginuser.getUid()));
        // 将token封装到result返回
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("token", token);

        return Result.success(datamap);
    }

    /**
     * 1.校验token的有效期,已经添加jwt拦截器，所以不需要校验token
     * 2.根据token解析出用户id
     * 3.根据用户id查询用户信息，返回请求体所需数据
     * 4.返回请求体所需数据(即去掉密码),封装result结果返回即可
     */
    @Override
    public Result getUserInfo(String token) {
        int loginUserId = jwtHelper.getUserId(token).intValue();

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUid, loginUserId);
        User loginuser = userMapper.selectOne(lambdaQueryWrapper);

        loginuser.setUserPwd("");

        // 根据响应json数据格式生成result
        // 包装user数据
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", loginuser.getUid());
        userData.put("username", loginuser.getUsername());
        userData.put("userPwd", loginuser.getUserPwd());
        userData.put("nickName", loginuser.getNickName());

        // 包装loginUser数据
        Map<String, Object> loginUserData = new HashMap<>();
        loginUserData.put("loginUser", userData);
        // 登录成功
        Result result = Result.success(loginUserData);
        return result;
    }

    /**
     * 注册用户名检查
     * 1.根据用户名查询
     * 2.返回count == 0(表示该用户名未占用) 可用
     * 3.返回count > 0  不可用
     */
    @Override
    public Result checkUserName(String username) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);

        if (userMapper.selectCount(lambdaQueryWrapper) > 0) {
            // 用户名被占用,账户已经存在
            // return Result.build(null, ResultCodeEnum.USERNAME_USED);
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
        // 用户名未占用，可以注册
        return Result.success(null);
    }

    /**
     * 注册功能
     * 1.检查账号是否被占用
     * 2.密码加密
     * 3.保存用户信息
     * 4.返回结果
     */
    @Override
    public Result register(UserDTO userDTO) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());

        if (userMapper.selectCount(lambdaQueryWrapper) > 0) {
            // 用户名被占用,账户已经存在
            // return Result.build(null, ResultCodeEnum.USERNAME_USED);
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 加密用户密码
        String md5pwd = MD5Util.encrypt(user.getUserPwd());
        user.setUserPwd(md5pwd);

        // 添加用户
        userMapper.insert(user);
        return Result.success(null);
    }
}




