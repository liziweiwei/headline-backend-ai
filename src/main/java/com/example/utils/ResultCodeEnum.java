package com.example.utils;

/**
 * 统一返回结果状态信息类
 */
public enum ResultCodeEnum {

    SUCCESS(200, "success"),
    USERNAME_ERROR(501, "usernameError"),
    PASSWORD_ERROR(503, "passwordError"),
    NOTLOGIN(504, "notLogin"),
    USERNAME_USED(505, "userNameUsed");
//    SUCCESS(200, "success"),
//    USERNAME_ERROR(501, "用户名错误"),
//    PASSWORD_ERROR(503, "密码错误"),
//    NOTLOGIN(504, "未登录"),
//    USERNAME_USED(505, "该账户已经存在");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
