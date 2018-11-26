package com.qingyun.download.multithread;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：带优先级参数的线程创建工厂
 */
public class PriorityThreadFactory implements ThreadFactory {
    private final int threadPriority;

    public PriorityThreadFactory(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Runnable wrapperRunnable =new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Process.setThreadPriority(threadPriority);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }
}
