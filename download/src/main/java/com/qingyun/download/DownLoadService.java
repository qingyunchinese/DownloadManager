package com.qingyun.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.qingyun.download.dao.DownLoadJob;
import com.qingyun.download.utils.LogUtil;

/**
 * 作者： android on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadService extends Service
{
    public static final String TAG = "DownLoadService";
    public static final String DOWNLOAD_URL = "com.android.download.url";
    public static final String DOWNLOAD_CACHE_PATH = "com.android.download.request.cachePath";
    public static final String DOWNLOAD_FILESIZE = "com.android.download.filesize";
    public static final String DOWNLOAD_FILE_CURRENT_SIZE = "com.android.download.filecurrentsize";
    public static final String DOWNLOAD_FILE_SPEED = "com.android.download.filespeed";
    public static final String DOWNLOAD_ERROR_MESSAGE = "com.android.download.error.errorMessage";
    public static final String ACTION_STOP_DOWNLOAD = "com.android.download.stop";
    public static final String ACTION_START_DOWNLOAD = "com.android.download.start";
    public static final String ACTION_STOP_ALL = "com.android.download.stop.all";
    private NotificationManager notificationManager = null;
    public static final int DOWNLOADNOTIFICATIONID = R.id.downLoadNotificationId;
    public final String ReceiverPermission = "com.qingyun.permission.downLoad";
    public DownLoadMultiRequest downLoadMultiRequest;
    public static final String DOWNLOAD_CHANNEL_ID = "android.downLoad.channel.id";
    public static final String DOWNLOAD_CHANNEL_NAME = "下载任务";
    public NotificationChannel notificationChannel;
    public NotificationCompat.Builder notificationCompatBuilder;

    @Override
    public void onCreate()
    {
        super.onCreate();
        LogUtil.v(TAG, "DownLoadService:onCreate");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        downLoadMultiRequest = DownLoadMultiRequest.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        LogUtil.v(TAG, "DownLoadService:onStartCommand");
        String downLoadUrl = "";
        String cacheFilePath = "";
        if (intent == null)
        {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null)
        {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (bundle.containsKey(DOWNLOAD_URL))
        {
            downLoadUrl = bundle.getString(DOWNLOAD_URL);
        }
        if (bundle.containsKey(DOWNLOAD_CACHE_PATH))
        {
            cacheFilePath = bundle.getString(DOWNLOAD_CACHE_PATH);
        }
        if (action.equals(ACTION_START_DOWNLOAD))
        {
            LogUtil.v(TAG, "DownLoadService:START_ACTION");
            LogUtil.v(TAG, "DownLoadService:downLoadUrl-->" + downLoadUrl);
            LogUtil.v(TAG, "DownLoadService:downLoadFilePath-->" + cacheFilePath);
            DownLoadJob downLoadJob = new DownLoadJob();
            downLoadJob.setCacheFilePath(cacheFilePath);
            downLoadJob.setDownLoadUrl(downLoadUrl);
            createNotificationChannel(false, "下载开始");
            startDownLoadJob(downLoadJob);
        }
        else if (action.equals(ACTION_STOP_DOWNLOAD))
        {
            LogUtil.v(TAG, "DownLoadService:STOP_ACTION");
            stopDownLoadJob(downLoadUrl);
        }
        else if (action.equals(ACTION_STOP_ALL))
        {
            LogUtil.v(TAG, "DownLoadService:ACTION_STOP_ALL");
            stopAllDownLoadJob();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 停止下载任务
     */
    public void stopDownLoadJob(String url)
    {
        downLoadMultiRequest.stopDownLoadJob(url);
    }

    /**
     * 停止所有下载任务
     */
    public void stopAllDownLoadJob()
    {
        downLoadMultiRequest.stopAllDownLoadJob();
        stopSelf();
    }

    public void cancelAll(boolean forceCancel)
    {
        downLoadMultiRequest.cancelAll(forceCancel);
    }

    @Override
    public void onDestroy()
    {
        LogUtil.v(TAG, "DownLoadService:onDestroy");
        cancelAll(true);
        notificationManager.cancel(DOWNLOADNOTIFICATIONID);
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        LogUtil.v(TAG, "DownLoadService:onLowMemory");
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        LogUtil.v(TAG, "DownLoadService:onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        LogUtil.v(TAG, "DownLoadService:onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        LogUtil.v(TAG, "DownLoadService:onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    private DownloadListener downloadListener = new DownloadListener()
    {
        @Override
        public void onDownLoadFailure(String url, String cacheFilePath, String strMsg)
        {
            LogUtil.v(TAG, "onDownLoadFailure");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_ERROR, url, cacheFilePath, strMsg);
            if (downLoadMultiRequest.isEmpty())
            {
                notificationManager.cancel(DOWNLOADNOTIFICATIONID);
                stopSelf();
            }
        }

        @Override
        public void onDownLoadLoading(String url, String cacheFilePath, long count, long current, long speed)
        {
            LogUtil.v(TAG, "onDownLoadLoading");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_LOADING, count, current, speed, url, cacheFilePath);
        }

        @Override
        public void onDownLoadStart(String url, String cacheFilePath)
        {
            LogUtil.v(TAG, "onDownLoadStart");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_START, url, cacheFilePath);
            displayNotification(false, "");
        }

        @Override
        public void onDownLoadSuccess(String url, String cacheFilePath)
        {
            LogUtil.v(TAG, "onDownLoadSuccess");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_SCUESS, url, cacheFilePath);
        }

        @Override
        public void onDownLoadStop(String url, String cacheFilePath)
        {
            LogUtil.v(TAG, "onDownLoadStop");
            sendBroadcast(DownloadBroadcastReceiver.ACTION_STOP, url, cacheFilePath);
            if (downLoadMultiRequest.isEmpty())
            {
                notificationManager.cancel(DOWNLOADNOTIFICATIONID);
                stopSelf();
            }
        }

        @Override
        public void onDownLoadFinish(String url, String cacheFilePath)
        {
            if (downLoadMultiRequest.isEmpty())
            {
                notificationManager.cancel(DOWNLOADNOTIFICATIONID);
                stopSelf();
            }
        }
    };

    /**
     * 开启下载任务
     */
    public void startDownLoadJob(DownLoadJob downLoadJob)
    {
        DownLoadRequest downLoadRequest = downLoadMultiRequest.getDownloadRequest(downLoadJob.getDownLoadUrl());
        if (downLoadRequest != null)
        {
            LogUtil.v(TAG, "重启下载任务:" + downLoadJob.toString());
            downLoadMultiRequest.addDownLoadJob(downLoadRequest);
        }
        else
        {
            LogUtil.v(TAG, "开启一个下载任务:" + downLoadJob.toString());
            downLoadRequest = new DownLoadRequest(downLoadJob);
            downLoadRequest.setDownloadListener(downloadListener);
            downLoadMultiRequest.addDownLoadJob(downLoadRequest);
        }
    }

    /**
     * 发送正在下载的广播
     */
    public void sendBroadcast(String action,
                              String downLoadUrl, String cachePath)
    {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        it.putExtras(bundle);
        it.setComponent(new ComponentName(this, "com.qingyun.download.DownloadBroadcastReceiver"));
        sendOrderedBroadcast(it, ReceiverPermission);
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
                              long count, long current, long speed, String downLoadUrl, String cachePath)
    {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        bundle.putLong(DOWNLOAD_FILESIZE, count);
        bundle.putLong(DOWNLOAD_FILE_CURRENT_SIZE, current);
        bundle.putLong(DOWNLOAD_FILE_SPEED, speed);
        it.setComponent(new ComponentName(this, "com.qingyun.download.DownloadBroadcastReceiver"));
        it.putExtras(bundle);
        sendOrderedBroadcast(it, ReceiverPermission);
    }

    /**
     * 发送下载失败的广播
     *
     * @param action
     * @param downLoadUrl
     * @param cachePath
     * @param errorMessage
     */
    public void sendBroadcast(String action, String downLoadUrl, String cachePath, String errorMessage)
    {
        Intent it = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(DOWNLOAD_URL, downLoadUrl);
        bundle.putString(DOWNLOAD_CACHE_PATH, cachePath);
        bundle.putString(DOWNLOAD_ERROR_MESSAGE, errorMessage);
        it.setComponent(new ComponentName(this, "com.qingyun.download.DownloadBroadcastReceiver"));
        it.putExtras(bundle);
        sendOrderedBroadcast(it, ReceiverPermission);
    }


    private void displayNotification(boolean isSound, String contentStr)
    {
        if (notificationCompatBuilder == null)
        {
            notificationCompatBuilder = new NotificationCompat.Builder(
                    this, DOWNLOAD_CHANNEL_ID);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.common_notification_large_icon);
        notificationCompatBuilder.setContentTitle("DownLoad")
                .setContentText(contentStr)
                .setTicker("进行中...")
                .setSmallIcon(R.drawable.common_notification_small_icon)
                .setLargeIcon(largeIcon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setOngoing(true);
        if (isSound)
        {
            notificationCompatBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }
        Notification notification = notificationCompatBuilder.build();
        notificationManager.notify(DOWNLOADNOTIFICATIONID, notification);
        startForeground(DOWNLOADNOTIFICATIONID, notification);
    }

    private void createNotificationChannel(boolean isSound, String contentStr)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            if (notificationCompatBuilder == null)
            {
                notificationChannel = new NotificationChannel(DOWNLOAD_CHANNEL_ID,
                        DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            }
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        displayNotification(isSound, contentStr);
    }
}
