package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.entity.Type;
import com.example.utils.Result;

/**
 * @author 胡金勇
 * @description 针对表【news_type】的数据库操作Service
 * @createDate 2024-03-20 20:45:23
 */
public interface TypeService extends IService<Type> {

    /**
     * 查询首页分类
     */
    Result findAllTypes();

}
