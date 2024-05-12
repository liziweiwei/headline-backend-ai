package com.example.exception;

public class AccountAlreadyExistsException extends BaseException {
    /**
     * AccountAlreadyExistsException 类的构造函数。
     * 该异常用于表示尝试创建一个已经存在的账户时的错误。
     */
    public AccountAlreadyExistsException() {
        // 默认构造函数，无需参数，仅初始化异常实例。
    }

    /**
     * 带有详细信息的 AccountAlreadyExistsException 构造函数。
     *
     * @param msg 异常信息字符串，用于描述账户已存在异常的详细情况。
     */
    public AccountAlreadyExistsException(String msg) {
        super(msg); // 调用父类异常的构造函数，将消息传递给父类。
    }
}
