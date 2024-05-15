package com.example.config;

import com.example.interceptors.LoginProtectedInterceptor;
import com.example.utils.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 在Spring Boot框架中,你不需要显式地添加@ComponentScan注解来指定basePackages
 * Spring Boot会自动扫描主类(带有@SpringBootApplication注解的类)所在包下的所有子包
 * 这是因为@SpringBootApplication注解本身就包含了@ComponentScan的功能,并且默认的行为是扫描主类所在的包及其子包
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginProtectedInterceptor loginProtectedInterceptor;

    /**
     * 向拦截器链注册新的拦截器。
     * 这个方法允许我们在Spring MVC的调度过程中添加自定义的拦截器，以实现诸如认证、日志记录等额外的功能。
     *
     * @param registry 代表拦截器注册表的InterceptorRegistry对象，用于注册新的拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(loginProtectedInterceptor)
                .addPathPatterns("/headline/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/portal/**")
                // 下面的界面未登录也能查看
                .excludePathPatterns("/user/login") // 用户登陆界面
                .excludePathPatterns("/portal/findAllTypes") // 查询首页分类
                .excludePathPatterns("/portal/findNewsPage")// 分页查询头条信息
                .excludePathPatterns("/headline/history");
        // TODO:此处逻辑后续要修改
        // .excludePathPatterns("/portal/showHeadlineDetail"); // 查询头条详情
    }

    /**
     * 扩展Spring MVC框架的消息转化器
     * 此方法用于向Spring MVC的请求响应消息转化器列表中添加自定义的消息转化器,以便支持额外的数据格式转换功能
     *
     * @param converters Spring MVC框架中已有的消息转化器列表，通过扩展此列表，可以增加自定义的消息转化器
     */
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("添加一个自定义JSON数据格式转换的消息转换器...");
        // 创建一个专门用于JSON数据转换的消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 为消息转换器配置自定义的对象转换器(ObjectMapper)，以定制JSON序列化和反序列化的细节
        // 对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将自定义的消息转换器添加到列表的最前面,以优先使用
        converters.add(0, converter);
    }

}
