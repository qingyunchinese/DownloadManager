package com.qingyun.download.multithread;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.qingyun.download.DownLoadRunnable;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419272@qq.com
 * 版本：v1.0
 * 描述：
 */
public class QYExecutor extends ThreadPoolExecutor {
    private static final int DEFAULT_THREAD_COUNT = 3;

    QYExecutor(int maxNumThreads, ThreadFactory threadFactory) {
        super(maxNumThreads, maxNumThreads, 0, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>(), threadFactory);
    }

    public void adjustThreadCount(NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            setThreadCount(DEFAULT_THREAD_COUNT);
            return;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                setThreadCount(4);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        setThreadCount(3);
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        setThreadCount(2);
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        setThreadCount(1);
                        break;
                    default:
                        setThreadCount(DEFAULT_THREAD_COUNT);
                }
                break;
            default:
                setThreadCount(DEFAULT_THREAD_COUNT);
        }
    }

    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }

    @Override
    public Future<?> submit(Runnable task) {
        AndroidFutureTask futureTask = new AndroidFutureTask((DownLoadRunnable) task);
        execute(futureTask);
        return futureTask;
    }

    private static final class AndroidFutureTask extends FutureTask<DownLoadRunnable>
            implements Comparable<AndroidFutureTask> {
        private final DownLoadRunnable hunter;

        public AndroidFutureTask(DownLoadRunnable hunter) {
            super(hunter, null);
            this.hunter = hunter;
        }

        @Override
        public int compareTo(AndroidFutureTask other) {
            ThreadPriority p1 = hunter.getPriority();
            ThreadPriority p2 = other.hunter.getPriority();
            return (p1 == p2 ? hunter.getAutoNumber() - other.hunter.getAutoNumber() : p2.ordinal() - p1.ordinal());
        }
    }
}
