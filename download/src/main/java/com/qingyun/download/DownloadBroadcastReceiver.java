package com.qingyun.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qingyun.download.utils.LogUtils;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "DownloadBroadcastReceiver";
    public static final String ACTION_START = "com.qingyun.download.action.start";
    public static final String ACTION_STOP = "com.qingyun.download.action.stop";
    public static final String ACTION_ERROR = "com.qingyun.download.action.error";
    public static final String ACTION_SCUESS = "com.qingyun.download.action.scuess";
    public static final String ACTION_LOADING = "com.qingyun.download.action.loading";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (!bundle.containsKey(DownLoadService.DOWNLOAD_URL)) {
            return;
        }
        dealDownLoadFile(action, bundle);
    }

    public void dealDownLoadFile(String action, Bundle bundle) {
        String downLoadUrl = "";
        String cachePath = "";
        if (bundle.containsKey(DownLoadService.DOWNLOAD_URL)) {
            downLoadUrl = bundle.getString(DownLoadService.DOWNLOAD_URL);
        }
        if (bundle.containsKey(DownLoadService.DOWNLOAD_CACHE_PATH)) {
            cachePath = bundle.getString(DownLoadService.DOWNLOAD_CACHE_PATH);
        }
        if (action.equals(ACTION_START)) {
            LogUtils.v(TAG,"ACTION_START:->"+downLoadUrl);
            QYDownLoadManager.getInstance().startDownLoadRequestDao(downLoadUrl,cachePath);
        } else if (action.equals(ACTION_STOP)) {
            LogUtils.v(TAG,"ACTION_STOP:->"+downLoadUrl);
            QYDownLoadManager.getInstance().stopDownLoadRequestDao(downLoadUrl,cachePath);
        } else if (action.equals(ACTION_ERROR)) {
            LogUtils.v(TAG,"ACTION_ERROR:->"+downLoadUrl);
            String errorMessage = bundle.getString(DownLoadService.DOWNLOAD_ERROR_MESSAGE);
            QYDownLoadManager.getInstance().errorDownLoadRequestDao(downLoadUrl,cachePath, errorMessage);
        } else if (action.equals(ACTION_SCUESS)) {
            LogUtils.v(TAG,"ACTION_SCUESS:->"+downLoadUrl);
            QYDownLoadManager.getInstance().finishDownLoadRequestDao(downLoadUrl,cachePath);
        } else if (action.equals(ACTION_LOADING)) {
            LogUtils.v(TAG,"ACTION_LOADING:->"+downLoadUrl);
            long fileSize = bundle
                    .getLong(DownLoadService.DOWNLOAD_FILESIZE);
            long fileCurrentSize = bundle
                    .getLong(DownLoadService.DOWNLOAD_FILE_CURRENT_SIZE);
            long speed = bundle
                    .getLong(DownLoadService.DOWNLOAD_FILE_SPEED);
            QYDownLoadManager.getInstance().loadingDownLoadFileLoading(downLoadUrl,cachePath, fileSize, fileCurrentSize, speed);
        }
    }
}
