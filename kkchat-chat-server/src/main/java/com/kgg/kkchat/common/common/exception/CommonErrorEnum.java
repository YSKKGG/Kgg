package com.kgg.kkchat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum{
    BUSINESS_ERROR(0,"{0}"),
    LOCK_LIMIT(-3,"请求太频繁了，请稍后"),
    SYSTEM_ERROR(-1,"系统出错了，sorry"),
    PARAM_INVALID(-2,"参数校验失败");

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
