package com.example.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeadlineHistoryDTO implements Serializable {

    private Integer uid;

    private String keyWords;
    // 默认值为0
    private Integer type = 0;
    // 默认值为1
    private Integer pageNum = 1;
    // 默认值为10
    private Integer pageSize = 10;
}
