package com.qingyun.multithread;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 * ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
 * int corePoolSize,//线程池中保留的线程的最小数量.
 * int maximumPoolSize,//线程池中所能容纳的最大线程数量。如果它的数量大于corePoolSize—并且当前的线程数量>= corePoolSize—那么新的工作线程会被继续创建只要任务队列中的任务足够多.
 * long keepAliveTime,//当线程的数量大于corePoolSize，非核心的线程（额外的空闲线程）将会等待新的任务，如果它们在这个参数定义的时间内没有等到一个新的任务，这些线程会被终结.
 * TimeUnit unit,//参数keepAliveTime的单位
 * BlockingQueue<Runnable> workQueue//任务队列，它只接收runnable任务。它必须是一个BlockingQueue
 * );
 */
public class DefaultExecutorSupplier implements  ExecutorSupplier{
    public static final int DEFAULT_MAX_NUM_THREADS = 2 * Runtime.getRuntime().availableProcessors() + 1;
    private final QYExecutor mNetworkExecutor;
    private final QYExecutor mImmediateNetworkExecutor;
    private final Executor mMainThreadExecutor;

    public DefaultExecutorSupplier() {
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
        mNetworkExecutor = new QYExecutor(DEFAULT_MAX_NUM_THREADS, backgroundPriorityThreadFactory);
        mImmediateNetworkExecutor = new QYExecutor(2, backgroundPriorityThreadFactory);
        mMainThreadExecutor = new MainThreadExecutor();
    }

    @Override
    public QYExecutor forNetworkTasks() {
        return mNetworkExecutor;
    }

    @Override
    public QYExecutor forImmediateNetworkTasks() {
        return mImmediateNetworkExecutor;
    }

    @Override
    public Executor forMainThreadTasks() {
        return mMainThreadExecutor;
    }
}
