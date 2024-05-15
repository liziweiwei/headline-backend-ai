package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class History implements Serializable {

    @TableId
    private Integer id;

    private Integer hid;

    private Integer uid;

    private Date browsingTime;

    // @TableLogic 因为在yaml中统一配置
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
