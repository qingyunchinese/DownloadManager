package com.qingyun.download;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadState {
    /** 等待下载... */
    public static final int init = 0;
    /** 开始下载... */
    public static final int start = 1;
    /** 正在下载中... */
    public static final int loading = 2;
    /** 下载完成... */
    public static final int success = 3;
    /** 下载失败... */
    public static final int error = 4;
    /** 停止下载...*/
    public static final int stop = 5;
    /** 下载完成...*/
    public static final int finish = 6;
}
