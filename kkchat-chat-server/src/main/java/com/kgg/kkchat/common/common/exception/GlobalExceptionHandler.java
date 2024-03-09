package com.kgg.kkchat.common.common.exception;

import cn.hutool.core.text.StrBuilder;
import com.kgg.kkchat.common.common.domain.vo.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<?> methodArgumentNotValidException(MethodArgumentNotValidException e){
        StrBuilder strBuilder = new StrBuilder();
        e.getBindingResult().getFieldErrors().forEach(x->strBuilder.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String errMsg = strBuilder.toString();
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode(), errMsg.substring(0, strBuilder.length()-1));
    }
    /**
     * 捕捉业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<?> businessException(BusinessException e){
        log.error("BusinessException exception! The reason is:{}",e.getMessage(),e);
        return ApiResult.fail(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * 捕捉最后的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> throwable(Throwable e){
        log.error("system exception! The reason is:{}",e.getMessage(),e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
}
