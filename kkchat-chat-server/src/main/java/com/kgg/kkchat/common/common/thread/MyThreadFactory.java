package com.kgg.kkchat.common.common.thread;


import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * Description:
 * Author: Kgg
 * Date: 2024/3/1
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER=new MyUncaughtExceptionHandler();
    private ThreadFactory original;
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);//执行spring自己的线程创建逻辑
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);//添加自己的创建逻辑
        return thread;
    }
}
