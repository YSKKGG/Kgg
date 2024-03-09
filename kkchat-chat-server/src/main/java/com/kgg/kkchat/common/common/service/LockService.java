package com.kgg.kkchat.common.common.service;

import com.kgg.kkchat.common.common.exception.BusinessException;
import com.kgg.kkchat.common.common.exception.CommonErrorEnum;
import jodd.time.TimeUtil;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/8
 */
@Service
public class LockService {
    @Autowired
    private RedissonClient redissonClient;

    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUtil, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean b = lock.tryLock(waitTime, timeUtil);
        if(!b){
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        }finally {
            lock.unlock();
        }
    }

    public <T> T executeWithLock(String key,Supplier<T> supplier) {
        return executeWithLock(key,-1,TimeUnit.MILLISECONDS,supplier);
    }

    public <T> T executeWithLock(String key,Runnable runnable) {
        return executeWithLock(key,-1,TimeUnit.MILLISECONDS,()->{
            runnable.run();
            return null;
        });
    }

    @FunctionalInterface
    public interface Supplier<T>{
        T get() throws Throwable;
    }
}
