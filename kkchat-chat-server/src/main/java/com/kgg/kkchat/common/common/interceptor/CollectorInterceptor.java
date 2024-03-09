package com.kgg.kkchat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.kgg.kkchat.common.common.domain.dto.RequestInfo;
import com.kgg.kkchat.common.common.utils.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/7
 */
@Component
public class CollectorInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID)).map(Object::toString).map(Long::parseLong).orElse(null);
        String clientIP = ServletUtil.getClientIP(request);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setIp(clientIP);
        requestInfo.setUid(uid);
        RequestHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
