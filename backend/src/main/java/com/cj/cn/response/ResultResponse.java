package com.cj.cn.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候, 如果是null的对象, key会消失
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse implements Serializable {
    private static final long serialVersionUID = -8559902472048882678L;
    private int status;
    private String msg;
    private Object data;

    private ResultResponse(int status) {
        this.status = status;
    }

    private ResultResponse(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    private ResultResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ResultResponse(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //使这个不在json序列化的结果中
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public static ResultResponse ok() {
        return new ResultResponse(ResponseCode.SUCCESS.getCode());
    }

    public static ResultResponse ok(Object data) {
        return new ResultResponse(ResponseCode.SUCCESS.getCode(), data);
    }

    public static ResultResponse ok(String successMsg) {
        return new ResultResponse(ResponseCode.SUCCESS.getCode(), successMsg);
    }

    public static ResultResponse ok(String msg, Object data) {
        return new ResultResponse(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static ResultResponse error(String errorMsg) {
        return new ResultResponse(ResponseCode.ERROR.getCode(), errorMsg);
    }

    public static ResultResponse error(int errorCode, String errorMsg) {
        return new ResultResponse(ResponseCode.ERROR.getCode(), errorMsg);
    }

    public static ResultResponse error(ResponseCode responseCode) {
        return new ResultResponse(responseCode.getCode(), responseCode.getDesc());
    }
}
