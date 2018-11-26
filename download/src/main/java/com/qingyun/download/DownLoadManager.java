package com.qingyun.download;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.qingyun.download.dao.DownLoadJob;
import com.qingyun.download.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadManager
{
    private final static String TAG = DownLoadManager.class.getSimpleName();
    private static DownLoadManager instance;
    private List<DownloadListener> downLoadFileListeners = new ArrayList<>();

    private DownLoadManager()
    {
        Collections.synchronizedList(downLoadFileListeners);
    }

    public static void init()
    {
        if (instance == null)
        {
            synchronized (DownLoadManager.class)
            {
                if (instance == null)
                {
                    instance = new DownLoadManager();
                }
            }
        }
    }

    public static DownLoadManager getInstance()
    {
        return instance;
    }

    public void startDownLoad(String downLoadUrl, String cachePath)
    {
        // TODO Auto-generated method stub
        DownLoadJob downLoadJob = getDownLoadDao(downLoadUrl);
        if (downLoadJob != null)
        {
            downLoadJob.setDownLoadState(DownLoadState.start);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadStart(downLoadUrl, cachePath);
                }
            }
        }
    }

    public void stopDownLoad(String downLoadUrl, String cachePath)
    {
        DownLoadJob downLoadJob = getDownLoadDao(downLoadUrl);
        if(downLoadJob!=null)
        {
            downLoadJob.setDownLoadState(DownLoadState.stop);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadStop(downLoadUrl, cachePath);
                }
            }
        }
    }


    public void errorDownLoad(String downLoadUrl, String cachePath, String errorMessage)
    {
        DownLoadJob downLoadJob = getDownLoadDao(downLoadUrl);
        if(downLoadJob!=null){
            downLoadJob.setDownLoadState(DownLoadState.error);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadFailure(downLoadUrl, cachePath, errorMessage);
                }
            }
        }
    }

    public void finishDownLoad(String downLoadUrl, String cachePath)
    {
        DownLoadJob downLoadJob = getDownLoadDao(downLoadUrl);
        if(downLoadJob!=null)
        {
            downLoadJob.setDownLoadState(DownLoadState.finish);
            if (!TextUtils.isEmpty(cachePath))
            {
                for (DownloadListener downLoadFileListener : downLoadFileListeners)
                {
                    if (downLoadFileListener != null)
                    {
                        downLoadFileListener.onDownLoadSuccess(downLoadUrl, cachePath);
                    }
                }
            }
            else
            {
                errorDownLoad(downLoadUrl, cachePath, "下载失败");
            }
        }
    }


    public void loadingDownLoading(String downLoadUrl, String cachePath, long fileSize, long fileCurrentSize, long speed)
    {
        // TODO Auto-generated method stub
        DownLoadJob downLoadJob = getDownLoadDao(downLoadUrl);
        if (downLoadJob != null)
        {
            downLoadJob.setDownLoadState(DownLoadState.loading);
            downLoadJob.setFileSize(fileSize);
            downLoadJob.setFileCurrentSize(fileCurrentSize);
            downLoadJob.setSpeed(speed);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadLoading(downLoadUrl, cachePath, fileSize,
                            fileCurrentSize, speed);
                }
            }
        }
    }

    public void registerDownLoadListener(
            DownloadListener listener)
    {
        if(!downLoadFileListeners.contains(listener)){
            downLoadFileListeners.add(listener);
        }
    }

    public void unregisterDownLoadListener(
            DownloadListener listener)
    {
        downLoadFileListeners.remove(listener);
    }


    public void downLoadFile(Context context, DownLoadJob downLoadJob)
    {
        LogUtil.v(TAG, "下载文件：" + downLoadJob.getDownLoadUrl());
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_START_DOWNLOAD);
        bundle.putString(DownLoadService.DOWNLOAD_URL, downLoadJob.getDownLoadUrl());
        bundle.putString(DownLoadService.DOWNLOAD_CACHE_PATH, downLoadJob.getCacheFilePath());
        it.putExtras(bundle);
        startService(context, it);
    }

    public void stopDownLoadFile(Context context, String downLoadUrl)
    {
        LogUtil.v(TAG, "停止下载文件：" + downLoadUrl);
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_STOP_DOWNLOAD);
        bundle.putString(DownLoadService.DOWNLOAD_URL, downLoadUrl);
        it.putExtras(bundle);
        startService(context, it);
    }

    public void stopAllDownLoadFile(Context context)
    {
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_STOP_ALL);
        it.putExtras(bundle);
        startService(context, it);
    }

    public DownLoadJob getDownLoadDao(String downloadUrl)
    {
        DownLoadRequest downLoadRequest = DownLoadMultiRequest.getInstance().getDownloadRequest(downloadUrl);
        if (downLoadRequest == null)
        {
            return null;
        }
        return downLoadRequest.getDownLoadJob();
    }


    private void startService(Context context, Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            context.startForegroundService(intent);
        }
        else
        {
            context.startService(intent);
        }
    }
}
