package com.example.interceptors;

import com.example.context.BaseContext;
import com.example.utils.JwtHelper;
import com.example.utils.Result;
import com.example.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * description: 登录包含拦截器，检查请求头是否包含有效token
 * 有效--放行
 * 无效--返回504
 */
@Component
public class LoginProtectedInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("当前线程id:" + Thread.currentThread().getId());

        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是Controller方法，直接放行
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("token");

        // 在token中获取用户id
        Long userId = jwtHelper.getUserId(token);
        // 将用户id存储到ThreadLocal(一个线程中的存储空间1)
        BaseContext.setCurrentId(userId);

        // 检查token是否有效
        if (!jwtHelper.isExpiration(token)) {
            return true;
        }

        // 无效返回504的状态json
        Result result = Result.build(null, ResultCodeEnum.NOTLOGIN);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().print(json);

        // 另一种格式
        // 添加后不再显示提示框
        // token无效,没有token或者token过期，重新设计返回给前端的参数,内容类型为JSON
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为401

        // Result result = Result.error(MessageConstant.USER_NOT_LOGIN);
        // 未登录,响应一个json给前端,这里不在controller层,无法直接返回,可以用jackson提供的 java对象和json字符串相互转换的对象映射类
        // 创建ObjectMapper实例，用于对象与JSON之间的转换
        // ObjectMapper objectMapper = new ObjectMapper();
        // 将Java对象转换为JSON字符串
        // String json = objectMapper.writeValueAsString(result);
        // 将JSON字符串写入HTTP响应中
        // response.getWriter().print(json);

        return false;
    }
}
