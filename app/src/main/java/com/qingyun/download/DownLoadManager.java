package com.qingyun.download;

import com.qingyun.utils.LogUtils;

import java.util.Stack;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadManager {

    private final static String TAG = DownLoadManager.class.getSimpleName();
    public static DownLoadManager instance;
    public DownLoadRequestQueue downLoadRequestQueue;
    private DownloadListener downloadListener;
    private Stack<DownLoadRequestDao> waitDownLoadRequestDaoStack = new Stack<>();

    private DownLoadManager() {
        downLoadRequestQueue = DownLoadRequestQueue.getInstance();
    }

    public static DownLoadManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadManager.class) {
                if (instance == null) {
                    instance = new DownLoadManager();
                }
            }
        }
        return instance;
    }

    /**
     * 开启下载任务
     */
    public void startDownLoadJob(DownLoadRequestDao downLoadRequestDao) {
        LogUtils.v(TAG, "开启一个下载任务:" + downLoadRequestDao.toString());
        DownLoadRequest downLoadRequest=downLoadRequestQueue.getDownloadRequest(downLoadRequestDao.getDownLoadUrl());
        if(downLoadRequest!=null){

        }else{
            downLoadRequest = new DownLoadRequest(downLoadRequestDao);
            downLoadRequest.setDownloadListener(downloadListener);
            downLoadRequestQueue.addDownLoadJob(downLoadRequest);
        }
    }

    /**
     * 停止下载任务
     */
    public void stopDownLoadJob(String url) {
        downLoadRequestQueue.stopDownLoadJob(url);
    }


    /**
     * 停止所有下载任务
     */
    public void stopAllDownLoadJob() {
        downLoadRequestQueue.stopAllDownLoadJob();
    }


    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void cancelAll(boolean forceCancel) {
        downLoadRequestQueue.cancelAll(forceCancel);
    }

}
