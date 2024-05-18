package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Recommendation implements Serializable {

    @TableId
    private Integer id;

    private Integer uid;

    private Integer type;

    private Double recommendCoefficient;

    private static final long serialVersionUID = 1L;
}
