package com.example.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局统一返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    // 返回码
    private Integer code;
    // 返回消息
    private String message;
    // 返回数据
    private T data;

    /**
     * 构建一个包含数据的结果对象。
     *
     * @param <T>  通用类型，代表结果对象中承载的数据类型。
     * @param data 用于构建结果对象的数据。如果数据非空，则会被设置到结果对象中。
     * @return 返回一个初始化后的结果对象，其中可能包含了传入的数据。
     */
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        // 判断数据是否非空，非空则设置到结果对象中
        if (data != null)
            result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    /**
     * 操作成功
     *
     * @param data baseCategory1List
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }


    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 异常时抛出的错误信息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(String msg) {
        return build(null, 0, msg);
    }
}