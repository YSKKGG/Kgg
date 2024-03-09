package com.kgg.kkchat.common.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/1
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread",e);
    }
}
