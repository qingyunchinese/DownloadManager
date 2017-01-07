package com.qingyun.download;

import com.qingyun.multithread.ThreadPriority;
import com.qingyun.utils.LogUtils;

import java.util.concurrent.Future;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRequest {
    private final static String TAG = DownLoadRequest.class.getSimpleName();
    private int autoNumber = 0;
    private ThreadPriority priority = null;
    private DownLoadRequestDao downLoadRequestDao;
    private Future future;
    /**
     * 回调接口
     */
    private DownloadListener downloadListener;
    /**
     * 是否取消
     */
    private boolean cancelled;

    public DownLoadRequest(DownLoadRequestDao downLoadRequestDao) {
        this.downLoadRequestDao = downLoadRequestDao;
    }

    public ThreadPriority getPriority() {
        return priority;
    }

    public void setPriority(ThreadPriority priority) {
        this.priority = priority;
    }


    public void setAutoNumber(int autoNumber) {
        this.autoNumber = autoNumber;
    }


    public void setFuture(Future future) {
        this.future = future;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel(boolean forceCancel) {
        try {
            if (forceCancel) {
                LogUtils.v(TAG, "cancelling request : " + toString());
                cancelled = true;
                if (future != null) {
                    future.cancel(true);
                }
            } else {
                cancelled = true;
                LogUtils.v(TAG, "not cancelling request : " + toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            onDownLoadStop(downLoadRequestDao.getDownLoadUrl(),downLoadRequestDao.getCacheFilePath());
        }
    }

    public void destroy() {
        downloadListener = null;
    }

    public void finish() {
        destroy();
        DownLoadRequestQueue.getInstance().finish(this);
    }


    public DownLoadRequestDao getDownLoadRequestDao() {
        return downLoadRequestDao;
    }


    public void onDownLoadFailure(String url,String cacheFilePath, String strMsg) {
        LogUtils.v(TAG,"onDownLoadFailure:->"+strMsg);
        if (downloadListener != null) {
            downloadListener.onDownLoadFailure(url,cacheFilePath, strMsg);
        }
        cancel(true);
        finish();
    }

    public void onDownLoadLoading(String url,String cacheFilePath,  long count, long current, long speed) {
        LogUtils.v(TAG,"onDownLoadLoading:->"+current);
        if (downloadListener != null) {
            downloadListener.onDownLoadLoading(url,cacheFilePath, count, current, speed);
        }
    }

    public void onDownLoadStart(String url,String cacheFilePath) {
        LogUtils.v(TAG,"onDownLoadStart");
        if (downloadListener != null) {
            downloadListener.onDownLoadStart(url,cacheFilePath);
        }
    }

    public void onDownLoadSuccess(String url,String cacheFilePath) {
        LogUtils.v(TAG,"onDownLoadSuccess");
        if (downloadListener != null) {
            downloadListener.onDownLoadSuccess(url,cacheFilePath);
        }
        cancel(true);
        finish();
    }

    public void onDownLoadStop(String url,String cacheFilePath) {
        LogUtils.v(TAG,"onDownLoadPause");
        if (downloadListener != null) {
            downloadListener.onDownLoadStop(url,cacheFilePath);
        }
        cancel(true);
        finish();
    }


    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        DownLoadRequest other = (DownLoadRequest) obj;
        if (other.downLoadRequestDao.getDownLoadUrl().equals(downLoadRequestDao.getDownLoadUrl())) {
            return true;
        }
        return false;
    }
}
