package com.qingyun.download;
import com.qingyun.download.multithread.MultiThreadCore;
import com.qingyun.download.okhttp.OkHttp3Utils;
import com.qingyun.download.utils.LogUtil;
import com.qingyun.download.multithread.QYExecutor;

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
public class DownLoadMultiRequest
{
    private final static String TAG = DownLoadMultiRequest.class.getSimpleName();
    private final Hashtable<String, DownLoadRequest> downloadRequests = new Hashtable<>();
    private AtomicInteger autoGenerator = new AtomicInteger();
    private static DownLoadMultiRequest sInstance;
    private OkHttpClient okHttpClient;

    private DownLoadMultiRequest()
    {
        okHttpClient = OkHttp3Utils.getClient();
    }

    public static DownLoadMultiRequest getInstance()
    {
        if (sInstance == null)
        {
            synchronized (DownLoadMultiRequest.class)
            {
                if (sInstance == null)
                {
                    sInstance = new DownLoadMultiRequest();
                }
            }
        }
        return sInstance;
    }

    private int getSequenceNumber()
    {
        return autoGenerator.incrementAndGet();
    }

    /**
     * 添加一个新的下载请求
     *
     * @param request
     */
    public DownLoadRequest addDownLoadJob(final DownLoadRequest request)
    {
        synchronized (downloadRequests)
        {
            try
            {
                LogUtil.v(TAG, "添加入下载队列");
                downloadRequests.put(request.getDownLoadJob().getDownLoadUrl(), request);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            request.setAutoNumber(getSequenceNumber());
            QYExecutor coreExecutor = MultiThreadCore.getInstance()
                    .getExecutorSupplier()
                    .forImmediateNetworkTasks();
            DownLoadRealRunnable downLoadRealRunnable = new DownLoadRealRunnable(okHttpClient, request);
            Future requestFuture = coreExecutor.submit(downLoadRealRunnable);
            request.setFuture(requestFuture);
            LogUtil.v(TAG, "下载队列数量:" + downloadRequests.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return request;
    }

    /**
     * 将该请求移出队列
     *
     * @param request
     */
    public void finish(DownLoadRequest request)
    {
        synchronized (downloadRequests)
        {
            try
            {
                LogUtil.v(TAG, "下载队列移除：" + request.getDownLoadJob().getDownLoadUrl());
                downloadRequests.remove(request.getDownLoadJob().getDownLoadUrl());
                LogUtil.v(TAG, "下载队列数量:" + downloadRequests.size());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止下载任务
     */
    public void stopDownLoadJob(String url)
    {
        DownLoadRequest downLoadRequest = downloadRequests.get(url);
        if (downLoadRequest != null)
        {
            downLoadRequest.cancel(false);
        }
    }

    /**
     * 停止下载任务
     */
    public void stopAllDownLoadJob()
    {
        cancelAll(false);
    }

    /**
     * 取消所有下载请求
     *
     * @param forceCancel
     */
    public void cancelAll(boolean forceCancel)
    {
        synchronized (downloadRequests)
        {
            try
            {
                Enumeration<String> enumeration = downloadRequests.keys();
                while (enumeration.hasMoreElements())
                {
                    String key = enumeration.nextElement();
                    DownLoadRequest request = downloadRequests.get(key);
                    request.cancel(forceCancel);
                    if (request.isCancelled())
                    {
                        request.destroy();
                        downloadRequests.remove(key);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public DownLoadRequest getDownloadRequest(String downLoadUrl)
    {
        return downloadRequests.get(downLoadUrl);
    }

    public boolean isEmpty()
    {
        return downloadRequests == null ? true : downloadRequests.isEmpty();
    }

}
