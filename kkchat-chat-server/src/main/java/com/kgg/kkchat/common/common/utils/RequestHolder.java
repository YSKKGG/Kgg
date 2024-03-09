package com.kgg.kkchat.common.common.utils;

import com.kgg.kkchat.common.common.domain.dto.RequestInfo;

/**
 * Description:请求上下文
 * Author: Kgg
 * Date: 2024/3/7
 */
public class RequestHolder {
    private static final ThreadLocal<RequestInfo> THREAD_LOCAL=new ThreadLocal<RequestInfo>();
    public static void set(RequestInfo requestInfo){
        THREAD_LOCAL.set(requestInfo);
    }

    public static RequestInfo get(){
        return THREAD_LOCAL.get();
    }

    public static void remove(){
         THREAD_LOCAL.remove();
    }
}
