package com.qingyun.download.okhttp;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 作者： qingyun on 16/11/27.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class BaseHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        // Always return true，接受任意域名服务器
        return true;
    }
}
