package com.qingyun.okhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * 作者： qingyun on 16/11/27.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class SSLSocketManager {

    public final String caFileName="";
    public final String caFileContent="";
    public volatile  static SSLSocketManager ssLSocketManager;

    public SSLSocketManager(){
    }

    public SSLSocketFactory getSSLSocketFactory(InputStream... certificates) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SSLContext sslContext = SSLContext.getInstance("TLS");

                TrustManagerFactory trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                trustManagerFactory.init(keyStore);
                sslContext.init(
                        null,
                        trustManagerFactory.getTrustManagers(),
                        new SecureRandom()
                );
                sslSocketFactory=sslContext.getSocketFactory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    public boolean useHttps(){
        if(!TextUtils.isEmpty(caFileName)||!TextUtils.isEmpty(caFileContent)){
            return true;
        }
        return false;
    }
}
