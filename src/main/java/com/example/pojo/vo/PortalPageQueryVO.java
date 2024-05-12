package com.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortalPageQueryVO implements Serializable {

    private Integer hid;

    private String title;

    private Integer type;

    private Integer pageViews;

    private Integer pastHours;

    private Integer publisher;
}
