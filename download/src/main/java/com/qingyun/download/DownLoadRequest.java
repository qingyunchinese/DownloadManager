package com.qingyun.download;

import com.qingyun.download.dao.DownLoadJob;
import com.qingyun.download.multithread.ThreadPriority;
import com.qingyun.download.utils.LogUtil;

import java.util.concurrent.Future;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRequest
{
    private final static String TAG = DownLoadRequest.class.getSimpleName();
    private int autoNumber = 0;
    private ThreadPriority priority = null;
    private DownLoadJob downLoadJob;
    private Future future;
    /**
     * 回调接口
     */
    private DownloadListener downloadListener;
    /**
     * 是否取消
     */
    private boolean cancelled;

    public DownLoadRequest(DownLoadJob downLoadJob)
    {
        this.downLoadJob = downLoadJob;
    }

    public ThreadPriority getPriority()
    {
        return priority;
    }

    public void setPriority(ThreadPriority priority)
    {
        this.priority = priority;
    }


    public void setAutoNumber(int autoNumber)
    {
        this.autoNumber = autoNumber;
    }


    public void setFuture(Future future)
    {
        this.future = future;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void cancel(boolean forceCancel)
    {
        try
        {
            if (forceCancel)
            {
                LogUtil.v(TAG, "cancelling request : " + toString());
                cancelled = true;
                if (future != null)
                {
                    future.cancel(true);
                }
            }
            else
            {
                cancelled = true;
                LogUtil.v(TAG, "not cancelling request : " + toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            onDownLoadStop(downLoadJob.getDownLoadUrl(), downLoadJob.getCacheFilePath());
        }
    }

    public void destroy()
    {
        downloadListener = null;
    }

    public void finish()
    {
        destroy();
        DownLoadMultiRequest.getInstance().finish(this);
    }


    public DownLoadJob getDownLoadJob()
    {
        return downLoadJob;
    }

    public void onDownLoadFinish(String url, String cacheFilePath)
    {
        LogUtil.v(TAG, "onDownLoadFinish:->" + url);
        DownLoadMultiRequest.getInstance().finish(this);
        if (downloadListener != null)
        {
            downloadListener.onDownLoadFinish(url, cacheFilePath);
        }
        destroy();
    }

    public void onDownLoadFailure(String url, String cacheFilePath, String strMsg)
    {
        LogUtil.v(TAG, "onDownLoadFailure:->" + strMsg);
        if (downloadListener != null)
        {
            downloadListener.onDownLoadFailure(url, cacheFilePath, strMsg);
        }
        cancel(true);
        onDownLoadFinish(url, cacheFilePath);
    }

    public void onDownLoadLoading(String url, String cacheFilePath, long count, long current, long speed)
    {
        LogUtil.v(TAG, "onDownLoadLoading:->" + current);
        if (downloadListener != null)
        {
            downloadListener.onDownLoadLoading(url, cacheFilePath, count, current, speed);
        }
    }

    public void onDownLoadStart(String url, String cacheFilePath)
    {
        LogUtil.v(TAG, "onDownLoadStart");
        if (downloadListener != null)
        {
            downloadListener.onDownLoadStart(url, cacheFilePath);
        }
    }

    public void onDownLoadSuccess(String url, String cacheFilePath)
    {
        LogUtil.v(TAG, "onDownLoadSuccess");
        if (downloadListener != null)
        {
            downloadListener.onDownLoadSuccess(url, cacheFilePath);
        }
        cancel(true);
        onDownLoadFinish(url, cacheFilePath);
    }

    public void onDownLoadStop(String url, String cacheFilePath)
    {
        LogUtil.v(TAG, "onDownLoadStop");
        if (downloadListener != null)
        {
            downloadListener.onDownLoadStop(url, cacheFilePath);
        }
        cancel(true);
        finish();
    }


    public void setDownloadListener(DownloadListener downloadListener)
    {
        this.downloadListener = downloadListener;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        DownLoadRequest other = (DownLoadRequest) obj;
        if (other.downLoadJob.getDownLoadUrl().equals(downLoadJob.getDownLoadUrl()))
        {
            return true;
        }
        return false;
    }
}
