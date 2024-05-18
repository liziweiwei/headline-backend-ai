package com.example.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserIdProperties {
    // 模拟用户id,默认是0,系统管理员
    // 该用户无浏览记录,各个类别的推荐系数均为0
    private Long userId = 0L;
}
