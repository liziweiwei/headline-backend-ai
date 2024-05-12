package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.entity.Type;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 胡金勇
 * @description 针对表【news_type】的数据库操作Mapper
 * @createDate 2024-03-20 20:45:23
 * @Entity com.example.pojo.entity.Type
 */
@Mapper
public interface TypeMapper extends BaseMapper<Type> {

}




