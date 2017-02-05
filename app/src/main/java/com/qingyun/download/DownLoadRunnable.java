package com.qingyun.download;

import android.text.TextUtils;

import com.qingyun.multithread.MultiThreadCore;
import com.qingyun.multithread.ThreadPriority;
import com.qingyun.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRunnable implements Runnable {
    private final static String TAG = DownLoadRunnable.class.getSimpleName();
    public ThreadPriority priority;
    public int autoNumber;
    private final int BUFFER_SIZE = 1024*200;
    /**
     * 文件下载Url
     */
    private String downLoadUrl = "";
    /**
     * 文件下载成功后文件
     */
    private File file;
    /**
     * 文件下载中临时文件
     */
    private File tempFile;
    /**
     * 文件下载保存的文件夹
     */
    private File baseDirFile;
    /**
     * 下载文件的总大小
     */
    private long totalSize = 0;
    /**
     * 已下载的文件大小
     */
    private long downloadSize = 0;
    /**
     * 下载前一时刻的时间
     */
    private long previousTime = 0;
    /**
     * 下载总时间
     */
    private long totalTime = 0;
    /**
     * 下载速度
     */
    private long netWorkSpeed = 0;
    /**
     * 前一时刻文件大小
     */
    private long previousFileSize = 0;
    /**
     * 下载进度通知时间频率
     */
    private static final int TIMERSLEEPTIME = 800;
    /**
     * 文件下载进度定时刷新任务
     */
    private Timer timer = new Timer();
    /**
     * 标识刷新进度任务是否继续
     */
    private boolean timerInterrupt = false;
    private OkHttpClient client;
    private Call call;
    private DownLoadRequest downLoadRequest;
    /**
     * 是否取消
     */
    private boolean cancelled;

    public DownLoadRunnable(OkHttpClient okHttpClient, DownLoadRequest downLoadRequest) {
        this.client = okHttpClient;
        this.downLoadRequest = downLoadRequest;
        DownLoadRequestDao downLoadRequestDao = downLoadRequest.getDownLoadRequestDao();
        downLoadUrl = downLoadRequestDao.getDownLoadUrl();
        file = new File(downLoadRequestDao.getCacheFilePath());
        tempFile = new File(downLoadRequestDao.getTempFilePath());
        baseDirFile = file.getParentFile();
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        if (!this.baseDirFile.exists()) {
            this.baseDirFile.mkdirs();
        }
    }

    @Override
    public void run() {
        Throwable error = null;
        String errorMessage = "";
        long result = -1;
        boolean needDownLoadThread = true;
        try {
            sendStartMessage(downLoadUrl);
            file.delete();
            if (file.exists()) {
                needDownLoadThread = false;
                downloadSize = totalSize;
                sendProgressMessage(downLoadUrl, totalSize, totalSize, 0);
                sendSuccessMessage(downLoadUrl);
            } else {
                if (tempFile.exists()) {
                    previousFileSize = tempFile.length();
                } else {
                    previousFileSize = 0;
                }
                Request.Builder builder = new Request.Builder().url(downLoadUrl);
                builder.addHeader("Content-Type",DownLoadUtils.getFileMediaType(downLoadUrl));
                String userAgent=downLoadRequest.getDownLoadRequestDao().getUserAgent();
                if(!TextUtils.isEmpty(userAgent)){
                    builder.addHeader("user-agent",downLoadRequest.getDownLoadRequestDao().getUserAgent());
                }
                builder.addHeader("RANGE",
                        "bytes=" + previousFileSize + "-");
                Request request = builder.build();
                call = client.newCall(request);
                if (call.isCanceled()) {
                    sendStopMessage(downLoadUrl);
                    return;
                } else {
                    Response response = call.execute();
                    int statusCode = response.code();
                    if (404 == statusCode) {
                        String detailMessage = DownLoadFileException
                                .getDetailMessage(404);
                        throw new DownLoadFileException("404", detailMessage);
                    }
                    if (410 == statusCode) {
                        String detailMessage = DownLoadFileException
                                .getDetailMessage(410);
                        throw new DownLoadFileException("410", detailMessage);
                    }
                    if (400 == statusCode) {
                        String detailMessage = DownLoadFileException
                                .getDetailMessage(400);
                        throw new DownLoadFileException("400", detailMessage);
                    }
                    if (405 == statusCode) {
                        String detailMessage = DownLoadFileException
                                .getDetailMessage(405);
                        throw new DownLoadFileException("405", detailMessage);
                    }
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        long contentLenght = responseBody.contentLength();
                        if (contentLenght == -1) {
                            contentLenght = responseBody.byteStream().available();
                        }
                        totalSize = contentLenght + previousFileSize;
                        downloadSize = previousFileSize;
                        startTimer();
                        previousTime = System.currentTimeMillis();
                        //写入到本地的数据流
                        BufferedSink sink;
                        if (tempFile.exists()) {
                            sink = Okio.buffer(Okio.appendingSink(tempFile));
                        } else {
                            sink = Okio.buffer(Okio.sink(tempFile));
                        }
                        int len;
                        //读取网络数据流
                        BufferedSource source = response.body().source();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int totalLen = 0;
                        while ((len = source.read(buffer)) != -1 && !cancelled) {
                            previousTime = System.currentTimeMillis();
                            sink.write(buffer, 0, len);
                            downloadSize += len;
                            totalLen = totalLen + len;
                            totalTime = System.currentTimeMillis() - previousTime;
                            if (totalTime > 0) {
                                netWorkSpeed = (long) ((len / totalTime) / 1.024);
                            }
                            result = len;
                        }
                        sink.flush();
                        source.close();
                        sink.close();
                        responseBody.close();
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            error = e;
            errorMessage = "无法解析主机地址";
        } catch (SocketException e) {
            error = e;
            errorMessage = "无法解析主机地址";
        } catch (SocketTimeoutException e) {
            error = e;
            errorMessage = "下载超时";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e;
            errorMessage = "文件保存地址有误";
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e;
        } catch (DownLoadFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e;
            errorMessage = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e;
            errorMessage = "IO数据流出错";
        } catch (Exception e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
            error = e;
            errorMessage = "未知类型错误";
        } finally {
            if (needDownLoadThread) {
                stopTimer();
                try {
                    Thread.sleep(TIMERSLEEPTIME);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (result == -1 || cancelled || error != null) {
                    if (!downLoadRequest.isCancelled()) {
                        sendFailureMessage(downLoadUrl, errorMessage);
                    } else {
                        sendStopMessage(downLoadUrl);
                    }
                    cancelled = true;
                    timerInterrupt = true;
                    return;
                }
                tempFile.renameTo(file);
                cancelled = true;
                timerInterrupt = true;
                sendProgressMessage(downLoadUrl, totalSize, downloadSize, netWorkSpeed);
                sendSuccessMessage("下载成功！");
            }
        }
    }


    public ThreadPriority getPriority() {
        return priority;
    }

    public int getAutoNumber() {
        return autoNumber;
    }

    public void sendStartMessage(final String downLoadUrl) {
        MultiThreadCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(() -> {
            if (downLoadRequest != null) {
                downLoadRequest.onDownLoadStart(downLoadUrl, file.getAbsolutePath());
            }
        });
    }

    protected void sendStopMessage(final String downLoadUrl) {
        MultiThreadCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(() -> {
            if (downLoadRequest != null) {
                downLoadRequest.onDownLoadStop(downLoadUrl, file.getAbsolutePath());
            }
        });
    }


    protected void sendSuccessMessage(final String downLoadUrl) {
        MultiThreadCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(() -> {
            if (downLoadRequest != null) {
                downLoadRequest.onDownLoadSuccess(downLoadUrl, file.getAbsolutePath());
            }
        });
    }

    protected void sendFailureMessage(final String downLoadUrl, final String erorMessage) {
        MultiThreadCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(() -> {
            if (downLoadRequest != null) {
                downLoadRequest.onDownLoadFailure(downLoadUrl, file.getAbsolutePath(), erorMessage);
            }
        });
    }

    protected void sendProgressMessage(final String downLoadUrl, final long totalSize, final long currentSize,
                                       final long speed) {
        MultiThreadCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(() -> {
            if (downLoadRequest != null) {
                downLoadRequest.onDownLoadLoading(downLoadUrl, file.getAbsolutePath(), totalSize, currentSize, speed);
            }
        });
    }

    /**
     * 开启定时任务，刷新文件下载进度
     */
    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (!timerInterrupt) {
                    sendProgressMessage(downLoadUrl, totalSize, downloadSize,
                            netWorkSpeed);
                    if (downLoadRequest.isCancelled()) {
                        cancelThread();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 500);
    }

    private void stopTimer() {
        timerInterrupt = true;
        if (timer != null) {
            timer.cancel();
        }
    }

    public void cancelThread() {
        LogUtils.v(TAG, "cancel");
        cancelled = false;
        if (call != null && call.isExecuted()) {
            call.cancel();
        }
    }
}
