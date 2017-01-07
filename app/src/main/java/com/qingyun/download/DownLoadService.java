package com.qingyun.download;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qingyun.utils.LogUtils;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadService extends Service {
    public static final String TAG = "DownLoadService";
    private DownLoadManager downloadManager;
    public static final String DOWNLOAD_URL = "com.qingyun.download.url";
    public static final String DOWNLOAD_CACHE_PATH = "com.qingyun.download.request.cachePath";
    public static final String DOWNLOAD_FILESIZE = "com.qingyun.download.filesize";
    public static final String DOWNLOAD_FILE_CURRENT_SIZE = "com.qingyun.download.filecurrentsize";
    public static final String DOWNLOAD_FILE_SPEED = "com.qingyun.download.filespeed";
    public static final String DOWNLOAD_ERROR_MESSAGE = "com.qingyun.download.error.message";
    public static final String ACTION_STOP_DOWNLOAD = "com.qingyun.download.stop";
    public static final String ACTION_START_DOWNLOAD = "com.qingyun.download.start";
    public static final String ACTION_STOP_ALL = "com.qingyun.download.stop.all";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "DownLoadService:onCreate");
        downloadManager = DownLoadManager.getInstance();
        downloadManager.setDownloadListener(downloadListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.v(TAG, "DownLoadService:onStartCommand");
        String downLoadUrl = "";
        String cacheFilePath = "";
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (bundle.containsKey(DownLoadConfig.DownLoadUrl)) {
            downLoadUrl = bundle.getString(DownLoadConfig.DownLoadUrl);
        }
        if (bundle.containsKey(DownLoadConfig.DownLoadCacheFilePath)) {
            cacheFilePath = bundle.getString(DownLoadConfig.DownLoadCacheFilePath);
        }
        if (action.equals(ACTION_START_DOWNLOAD)) {
            LogUtils.v(TAG, "DownLoadService:START_ACTION");
            LogUtils.v(TAG, "DownLoadService:downLoadUrl-->" + downLoadUrl);
            LogUtils.v(TAG, "DownLoadService:downLoadFilePath-->" + cacheFilePath);
            DownLoadRequestDao downLoadRequestDao = new DownLoadRequestDao();
            downLoadRequestDao.setCacheFilePath(cacheFilePath);
            downLoadRequestDao.setDownLoadUrl(downLoadUrl);
            downloadManager.startDownLoadJob(downLoadRequestDao);
        } else if (action.equals(ACTION_STOP_DOWNLOAD)) {
            LogUtils.v(TAG, "DownLoadService:STOP_ACTION");
            downloadManager.stopDownLoadJob(downLoadUrl);
        } else if (action.equals(ACTION_STOP_ALL)) {
            downloadManager.stopAllDownLoadJob();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.v(TAG, "DownLoadService:onDestroy");
        downloadManager.cancelAll(true);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        LogUtils.v(TAG, "DownLoadService:onLowMemory");
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.v(TAG, "DownLoadService:onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.v(TAG, "DownLoadService:onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtils.v(TAG, "DownLoadService:onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownLoadFailure(String url, String cacheFilePath, String strMsg) {
            LogUtils.v(TAG, "onDownLoadFailure");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_ERROR, url, cacheFilePath, strMsg);
        }

        @Override
        public void onDownLoadLoading(String url, String cacheFilePath, long count, long current, long speed) {
            LogUtils.v(TAG, "onDownLoadLoading");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_LOADING, count, current, speed, url, cacheFilePath);
        }

        @Override
        public void onDownLoadStart(String url, String cacheFilePath) {
            LogUtils.v(TAG, "onDownLoadStart");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_START, url, cacheFilePath);
        }

        @Override
        public void onDownLoadSuccess(String url, String cacheFilePath) {
            LogUtils.v(TAG, "onDownLoadSuccess");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_SCUESS, url, cacheFilePath);
        }

        @Override
        public void onDownLoadStop(String url, String cacheFilePath) {
            LogUtils.v(TAG, "onDownLoadStop");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_STOP, url, cacheFilePath);
        }
    };

    /**
     * 发送正在下载的广播
     */
    public void sendBroadcast(String action,
                              String downLoadUrl, String cachePath) {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        it.putExtras(bundle);
        sendOrderedBroadcast(it, null);
    }

    /**
     * 发送正在下载的广播
     *
     * @param action
     * @param count
     * @param current
     * @param speed
     */
    public void sendBroadcast(String action,
                              long count, long current, long speed, String downLoadUrl, String cachePath) {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        bundle.putLong(DOWNLOAD_FILESIZE, count);
        bundle.putLong(DOWNLOAD_FILE_CURRENT_SIZE, current);
        bundle.putLong(DOWNLOAD_FILE_SPEED, speed);
        it.putExtras(bundle);
        sendOrderedBroadcast(it, null);
    }

    /**
     * 发送下载失败的广播
     *
     * @param action
     * @param downLoadUrl
     * @param cachePath
     * @param errorMessage
     */
    public void sendBroadcast(String action, String downLoadUrl, String cachePath, String errorMessage) {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        bundle.putString(DOWNLOAD_ERROR_MESSAGE, errorMessage);
        it.putExtras(bundle);
        sendOrderedBroadcast(it, null);
    }
}
