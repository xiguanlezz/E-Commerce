package com.cj.cn.exception;

public class BaseException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public BaseException(String message) {
        super(message);
        this.message = message;
    }
}
