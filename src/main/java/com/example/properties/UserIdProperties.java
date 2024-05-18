package com.example.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserIdProperties {
    private Long userId = 11L;
}
