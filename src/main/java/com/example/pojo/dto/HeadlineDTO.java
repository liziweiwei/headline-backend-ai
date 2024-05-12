package com.example.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeadlineDTO implements Serializable {

    private String title;

    private String article;

    private Integer type;
}
