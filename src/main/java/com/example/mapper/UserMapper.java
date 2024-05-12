package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 胡金勇
 * @description 针对表【news_user】的数据库操作Mapper
 * @createDate 2024-03-20 20:45:23
 * @Entity com.example.pojo.entity.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




