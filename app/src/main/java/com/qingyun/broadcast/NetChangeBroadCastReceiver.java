package com.qingyun.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

import com.qingyun.multithread.MultiThreadCore;
import com.qingyun.multithread.QYExecutor;

/**
 * 作者： qingyun on 17/1/7.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class NetChangeBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        QYExecutor qyExecutor = MultiThreadCore.getInstance()
                .getExecutorSupplier()
                .forImmediateNetworkTasks();
        qyExecutor.adjustThreadCount(cm.getActiveNetworkInfo());
        State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED == mobileState) {
            Toast.makeText(context, "手机网络连接成功", Toast.LENGTH_SHORT).show();
        } else if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED != mobileState) {
            Toast.makeText(context, "手机没有任何的网络", Toast.LENGTH_SHORT).show();
        } else if (wifiState != null && State.CONNECTED == wifiState) {
            Toast.makeText(context, "WIFI连接成功", Toast.LENGTH_SHORT).show();
        }
    }
}
