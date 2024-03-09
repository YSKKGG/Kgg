package com.kgg.kkchat.common.common.aspect;

import com.kgg.kkchat.common.common.annotation.RedissonLock;
import com.kgg.kkchat.common.common.service.LockService;
import com.kgg.kkchat.common.common.utils.SpElUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/8
 */
@Component
@Aspect
@Order(0)//确保事务比注解先执行
public class RedissonLockAspect {
    @Autowired
    private LockService lockService;

    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable{
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String prefix= StringUtils.isBlank(redissonLock.prefixKey())? SpElUtil.getMethodKey(method):redissonLock.prefixKey();
        String key=SpElUtil.parseSpEl(method,joinPoint.getArgs(),redissonLock.key());
        return lockService.executeWithLock(prefix+":"+key,redissonLock.waitTime(),redissonLock.unit(),joinPoint::proceed);
    }
}
