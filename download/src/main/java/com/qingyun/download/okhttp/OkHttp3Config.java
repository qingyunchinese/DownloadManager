package com.qingyun.download.okhttp;

import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import com.qingyun.download.BuildConfig;
import com.qingyun.download.QYDownLoadManager;
import com.qingyun.download.utils.NetWorkUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者： qingyun on 16/12/7.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class OkHttp3Config {
    /**
     * 网络请求交互日志TAG
     */
    public static final String TAG = "OkHttp3";
    /**
     * 网络请求超时时间
     */
    public final static int TIMEOUT = 15;
    /**
     * 写入操作请求超时时间
     */
    public final static int WRITE_TIMEOUT = 15;
    /**
     * 读取操作请求超时时间
     */
    public final static int READ_TIMEOUT = 15;
    /**
     * 缓存文件名
     */
    public final static String HTTP_CACHE_FILENAME = "okhttpCache";
    /**
     * 最多缓存5M的文件
     */
    public final static int HTTP_CACHE_MAXSIZE = 5 * 1024 * 1024;


    public static Cache getDefaultCache() {
        File httpCacheDirectory = new File(QYDownLoadManager.getInstance().getCacheDir(), HTTP_CACHE_FILENAME);
        return new Cache(httpCacheDirectory, HTTP_CACHE_MAXSIZE);
    }

    /**
     * 缓存拦截器
     */
    @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
    public static final Interceptor CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheTime = request.header("Cache-Time");
            if(TextUtils.isEmpty(cacheTime)){
                return chain.proceed(request);
            }
            if (!NetWorkUtils.isConnected(QYDownLoadManager.getInstance().getContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (!NetWorkUtils.isConnected(QYDownLoadManager.getInstance().getContext())) {
                // 无网络时 设置缓存超时时间0个小时
                response.newBuilder()
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=0")
                        .build();
            }else {
                // 有网络时，设置超时为cacheTime
                response.newBuilder()
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + cacheTime)
                        .build();
            }
            return response;
        }
    };

    /**
     * 日志拦截器
     */
    public static final Interceptor LOGGING_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            logHttp(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            logHttp(String.format("Received response for %s in %.1fms%n%sconnection=%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers(), chain.connection()));
            return response;
        }
    };

    /**
     * HTTP请求协议头信息拦截
     */
    public static final Interceptor ADD_HEAD_PARAMETER_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), originalRequest.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    };

    /**
     * log日志输出
     * @param log
     */
    public static void logHttp(String log) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, log);
        }
    }

}
