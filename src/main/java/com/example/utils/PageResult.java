package com.example.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    private List records; // 当前页数据集合

    private Integer pageNum; // 当前页码

    private Integer pageSize; // 每页显示条数

    private Integer totalPage; // 总页数

    private Integer totalSize; // 总记录条数

}