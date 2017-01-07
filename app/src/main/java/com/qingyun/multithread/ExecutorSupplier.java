package com.qingyun.multithread;

import java.util.concurrent.Executor;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public interface ExecutorSupplier {
    QYExecutor forNetworkTasks();

    QYExecutor forImmediateNetworkTasks();

    Executor forMainThreadTasks();
}
