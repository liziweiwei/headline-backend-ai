package com.example.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class HeadlineUpdateDTO implements Serializable {

    private Integer hid;

    private String title;

    private String article;

    private Integer type;
}
