package com.example.handler;

import com.example.exception.BaseException;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    public Result exceptionHandler(BaseException e) {

        log.error("异常信息:{}", e.getMessage());

        String msg = e.getMessage();
        return Result.error(msg);
    }
}
