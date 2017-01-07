package com.qingyun.download;

/**
 * 作者： qingyun on 17/1/2.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public interface DownloadListener {
    /**
     * 下载失败回调
     * @param url
     * @param strMsg
     */
    public void onDownLoadFailure(String url,String cacheFilePath,String strMsg);

    /**
     * 下载进度回调
     * @param url
     * @param count
     * @param current
     * @param speed
     */
    public void onDownLoadLoading(String url,String cacheFilePath, long count, long current, long speed);

    /**
     * 下载任务开始
     * @param url
     */
    public void onDownLoadStart(String url,String cacheFilePath);

    /**
     * 下载任务成功
     * @param url
     */
    public void onDownLoadSuccess(String url,String cacheFilePath);


    /**
     * 下载任务暂停
     * @param url
     */
    public void onDownLoadStop(String url,String cacheFilePath);

}
