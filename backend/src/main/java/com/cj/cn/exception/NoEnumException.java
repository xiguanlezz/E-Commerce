package com.cj.cn.exception;

public class NoEnumException extends BaseException {
    private String message;

    public NoEnumException(String message) {
        super(message);
    }
}
