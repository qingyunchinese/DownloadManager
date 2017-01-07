package com.qingyun.download;

import com.qingyun.multithread.MultiThreadCore;
import com.qingyun.multithread.QYExecutor;
import com.qingyun.okhttp.OkHttp3Utils;
import com.qingyun.utils.LogUtils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRequestQueue {
    private final static String TAG = DownLoadRequestQueue.class.getSimpleName();
    private final Hashtable<String, DownLoadRequest> downloadRequests = new Hashtable<>();
    private AtomicInteger autoGenerator = new AtomicInteger();
    private static DownLoadRequestQueue sInstance;
    private OkHttpClient okHttpClient;

    private DownLoadRequestQueue() {
        okHttpClient = OkHttp3Utils.getClient();
    }

    public static DownLoadRequestQueue getInstance() {
        if (sInstance == null) {
            synchronized (DownLoadRequestQueue.class) {
                if (sInstance == null) {
                    sInstance = new DownLoadRequestQueue();
                }
            }
        }
        return sInstance;
    }

    private int getSequenceNumber() {
        return autoGenerator.incrementAndGet();
    }

    /**
     * 添加一个新的下载请求
     *
     * @param request
     */
    public DownLoadRequest addDownLoadJob(final DownLoadRequest request) {
        synchronized (downloadRequests) {
            try {
                LogUtils.v(TAG, "添加入下载队列");
                downloadRequests.put(request.getDownLoadRequestDao().getDownLoadUrl(), request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            request.setAutoNumber(getSequenceNumber());
            QYExecutor qyExecutor = MultiThreadCore.getInstance()
                    .getExecutorSupplier()
                    .forImmediateNetworkTasks();
            DownLoadRunnable downLoadRunnable = new DownLoadRunnable(okHttpClient, request);
            Future requestFuture = qyExecutor.submit(downLoadRunnable);
            request.setFuture(requestFuture);
            LogUtils.v(TAG, "下载队列数量:" + downloadRequests.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    /**
     * 将该请求移出队列
     *
     * @param request
     */
    public void finish(DownLoadRequest request) {
        synchronized (downloadRequests) {
            try {
                LogUtils.v(TAG, "下载队列移除：" + request.getDownLoadRequestDao().getDownLoadUrl());
                downloadRequests.remove(request.getDownLoadRequestDao().getDownLoadUrl());
                LogUtils.v(TAG, "下载队列数量:" + downloadRequests.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止下载任务
     */
    public void stopDownLoadJob(String url) {
        DownLoadRequest downLoadRequest = downloadRequests.get(url);
        if (downLoadRequest != null) {
            downLoadRequest.cancel(false);
        }
    }

    /**
     * 停止下载任务
     */
    public void stopAllDownLoadJob() {
        cancelAll(false);
    }

    /**
     * 取消所有下载请求
     *
     * @param forceCancel
     */
    public void cancelAll(boolean forceCancel) {
        synchronized (downloadRequests) {
            try {
                Enumeration<String> enumeration = downloadRequests.keys();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    DownLoadRequest request = downloadRequests.get(key);
                    request.cancel(forceCancel);
                    if (request.isCancelled()) {
                        request.destroy();
                        downloadRequests.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DownLoadRequest getDownloadRequest(String downLoadUrl) {
        return downloadRequests.get(downLoadUrl);
    }

}
