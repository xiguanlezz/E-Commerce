package com.cj.cn.exception;

import com.cj.cn.response.ResponseCode;
import com.cj.cn.response.ResultResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.BindException;
import java.util.List;

/**
 * ControllerAdvice注解只能用于处理全局异常信息, 和普通控制器不同
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object handleErrors(Exception e) {
        if (e instanceof BindException) {
            return ResultResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = ex.getBindingResult();
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            StringBuilder resultMsg = new StringBuilder();
            int size = allErrors.size();
            for (int i = 0; i < size; i++) {
                resultMsg.append(allErrors.get(i).getDefaultMessage());
                if (i != size - 1)
                    resultMsg.append(" | ");
            }
            return ResultResponse.error(resultMsg.toString());
        } else if (e instanceof NoEnumException) {
            return ResultResponse.error("状态码出现未知错误");
        }
        return null;
    }
}