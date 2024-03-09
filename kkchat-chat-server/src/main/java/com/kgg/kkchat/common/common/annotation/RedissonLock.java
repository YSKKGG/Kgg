package com.kgg.kkchat.common.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Description:分布式锁
 * Author: Kgg
 * Date: 2024/3/8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedissonLock {

    /**
     * key的前缀，默认取方法全限定名，可以自己制定
     */
    String prefixKey() default "";

    /**
     * 支持springEl表达式的key
     */
    String key();

    /**
     * 等待锁的排队时间，默认快速失败
     */
    int waitTime() default -1;

    /**
     * 时间单位，默认毫秒
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

}
