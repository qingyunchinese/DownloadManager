package com.qingyun.download.multithread;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class MultiThreadCore {
    private static MultiThreadCore sInstance = null;
    private final ExecutorSupplier mExecutorSupplier;

    private MultiThreadCore() {
        this.mExecutorSupplier = new DefaultExecutorSupplier();
    }

    public static MultiThreadCore getInstance() {
        if (sInstance == null) {
            synchronized (MultiThreadCore.class) {
                if (sInstance == null) {
                    sInstance = new MultiThreadCore();
                }
            }
        }
        return sInstance;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return mExecutorSupplier;
    }

    public static void shutDown() {
        if (sInstance != null) {
            sInstance = null;
        }
    }
}

