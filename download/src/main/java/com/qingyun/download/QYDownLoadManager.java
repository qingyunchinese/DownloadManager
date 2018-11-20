package com.qingyun.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qingyun.download.db.GreenDaoUtils;
import com.qingyun.download.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class QYDownLoadManager
{
    private Context context;
    private final static String TAG = QYDownLoadManager.class.getSimpleName();
    private HashMap<String, DownLoadRequestDao> downloadRequestMap = new HashMap<>();
    private static QYDownLoadManager instance;
    private List<DownloadListener> downLoadFileListeners = new ArrayList<>();

    private QYDownLoadManager(@NotNull Context context)
    {
        downloadRequestMap = new HashMap<>();
        context = context.getApplicationContext();
    }

    public static void init(@NotNull Context context)
    {
        if (instance == null)
        {
            synchronized (QYDownLoadManager.class)
            {
                if (instance == null)
                {
                    instance = new QYDownLoadManager(context);
                }
            }
        }
    }

    public String getCacheDir(){
        return "";
    }

    public Context getContext()
    {
        return context;
    }

    public static QYDownLoadManager getInstance()
    {
        return instance;
    }

    public DownLoadRequestDao getDownLoadUrlState(String downLoadUrl)
    {
        return downloadRequestMap.get(downLoadUrl);
    }

    public List<DownLoadRequestDao> initHistoryData()
    {
        List<DownLoadRequestDao> daoList = GreenDaoUtils.getSingleTon().getAll();
        for (DownLoadRequestDao downLoadRequestDao : daoList)
        {
            downloadRequestMap.put(downLoadRequestDao.getDownLoadUrl(), downLoadRequestDao);
        }
        return daoList;
    }

    public void startDownLoadRequestDao(String downLoadUrl, String cachePath)
    {
        // TODO Auto-generated method stub
        DownLoadRequestDao downLoadRequestDao = downloadRequestMap.get(downLoadUrl);
        if (downLoadRequestDao != null)
        {
            downLoadRequestDao.setDownLoadState(DownLoadState.start);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadStart(downLoadUrl, cachePath);
                }
            }
        }
    }

    public void stopDownLoadRequestDao(String downLoadUrl, String cachePath)
    {
        // TODO Auto-generated method stub
        DownLoadRequestDao downLoadRequestDao = downloadRequestMap.get(downLoadUrl);
        if (downLoadRequestDao != null)
        {
            downLoadRequestDao.setDownLoadState(DownLoadState.stop);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadStop(downLoadUrl, cachePath);
                }
            }
        }
    }


    public void errorDownLoadRequestDao(String downLoadUrl, String cachePath, String errorMessage)
    {
        // TODO Auto-generated method stub
        DownLoadRequestDao downLoadRequestDao = downloadRequestMap.get(downLoadUrl);
        if (downLoadRequestDao != null)
        {
            downLoadRequestDao.setDownLoadState(DownLoadState.error);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadFailure(downLoadUrl, cachePath, errorMessage);
                }
            }
        }
    }

    public void finishDownLoadRequestDao(String downLoadUrl, String cachePath)
    {
        // TODO Auto-generated method stub
        DownLoadRequestDao downLoadRequestDao = downloadRequestMap.get(downLoadUrl);
        if (downLoadRequestDao != null)
        {
            downLoadRequestDao.setDownLoadState(DownLoadState.scuess);
            for (DownloadListener downLoadFileListener : downLoadFileListeners)
            {
                if (downLoadFileListener != null)
                {
                    downLoadFileListener.onDownLoadSuccess(downLoadUrl, cachePath);
                }
            }
        }
    }


    public void loadingDownLoadFileLoading(String downLoadUrl, String cachePath, long fileSize, long fileCurrentSize, long speed)
    {
        // TODO Auto-generated method stub
        DownLoadRequestDao downLoadRequestDao = downloadRequestMap.get(downLoadUrl);
        if (downLoadRequestDao != null)
        {
            downLoadRequestDao.setDownLoadState(DownLoadState.loading);
            downLoadRequestDao.setFileSize(fileSize);
            downLoadRequestDao.setFileCurrentSize(fileCurrentSize);
            downLoadRequestDao.setSpeed(speed);
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

    public void registerDownLaodListener(
            DownloadListener listener)
    {
        downLoadFileListeners.add(listener);
    }

    public void unregisterDownLaodListener(
            DownloadListener listener)
    {
        downLoadFileListeners.remove(listener);
    }


    public void downLoadFile(Context context, DownLoadRequestDao downLoadRequestDao)
    {
        LogUtils.v(TAG, "下载文件：" + downLoadRequestDao.getDownLoadUrl());
        insertDao(downLoadRequestDao);
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_START_DOWNLOAD);
        bundle.putString(DownLoadConfig.DownLoadUrl, downLoadRequestDao.getDownLoadUrl());
        bundle.putString(DownLoadConfig.DownLoadFileName, downLoadRequestDao.getFileName());
        bundle.putString(DownLoadConfig.DownLoadCacheFilePath, downLoadRequestDao.getCacheFilePath());
        it.putExtras(bundle);
        context.startService(it);
    }

    public void stopDownLoadFile(Context context, String downLoadUrl)
    {
        LogUtils.v(TAG, "停止下载文件：" + downLoadUrl);
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_STOP_DOWNLOAD);
        bundle.putString(DownLoadConfig.DownLoadUrl, downLoadUrl);
        it.putExtras(bundle);
        context.startService(it);
    }

    public void stopAllDownLoadFile(Context context)
    {
        Intent it = new Intent(context, DownLoadService.class);
        Bundle bundle = new Bundle();
        it.setAction(DownLoadService.ACTION_STOP_ALL);
        it.putExtras(bundle);
        context.startService(it);
    }

    private void insertDao(DownLoadRequestDao downLoadRequestDao)
    {
        if (!downloadRequestMap.containsKey(downLoadRequestDao.getDownLoadUrl()))
        {
            downloadRequestMap.put(downLoadRequestDao.getDownLoadUrl(), downLoadRequestDao);
            GreenDaoUtils.getSingleTon().insert(downLoadRequestDao);
        }
    }
}
