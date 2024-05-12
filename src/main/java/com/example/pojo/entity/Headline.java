package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

// @TableName(value ="news_headline") 因为在yaml中统一配置
@Data
public class Headline implements Serializable {
    @TableId
    private Integer hid;

    private String title;

    private String article;

    private Integer type;

    private Integer publisher;

    private Integer pageViews;

    private Date createTime;

    private Date updateTime;

    @Version
    private Integer version;

    // @TableLogic 因为在yaml中统一配置
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}