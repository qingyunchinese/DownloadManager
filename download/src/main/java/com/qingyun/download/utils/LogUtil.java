package com.qingyun.download.utils;

import com.qingyun.download.BuildConfig;

/**
 * 作者： qingyun on 16/11/23.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class LogUtil
{
    public static boolean isDebug = BuildConfig.DEBUG;
    public static final String TAG = "download";

    public static void v(String tag, String msg)
    {
        if (isDebug)
        {
            android.util.Log.v(TAG, tag + " : " + msg);
        }
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
        {
            android.util.Log.e(TAG, tag + " : " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable t)
    {
        if (isDebug)
        {
            android.util.Log.e(TAG, tag + " : " + msg, t);
        }
    }
}
