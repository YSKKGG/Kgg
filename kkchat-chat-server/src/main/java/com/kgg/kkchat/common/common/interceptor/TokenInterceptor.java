package com.kgg.kkchat.common.common.interceptor;

import cn.hutool.http.ContentType;
import com.kgg.kkchat.common.common.exception.HttpErrorEnum;
import com.kgg.kkchat.common.user.service.LoginService;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * Description:拦截器
 * Author: Kgg
 * Date: 2024/3/6
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";

    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if(Objects.nonNull(validUid)){
            //用户已登录
            request.setAttribute(UID,validUid);
        }else {
            //用户未登录
            boolean isPublicUri = isPublicUri(request);
            if(!isPublicUri){
                //401
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    private static boolean isPublicUri(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/");
        boolean isPublicUri = split.length>3&&"public".equals(split[3]);
        return isPublicUri;
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(header)
                .filter(h->h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h->h.replaceFirst(AUTHORIZATION_SCHEMA,""))
                .orElse(null);
    }
}
