package com.qingyun.download.okhttp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 作者： qingyun on 16/12/7.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class OkHttp3Utils {
    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (OkHttp3Utils.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(OkHttp3Config.TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(OkHttp3Config.READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(OkHttp3Config.WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(OkHttp3Config.LOGGING_INTERCEPTOR)
                            .addInterceptor(OkHttp3Config.ADD_HEAD_PARAMETER_INTERCEPTOR)
                    .build();
                }
            }
        }
        return client;
    }


}
