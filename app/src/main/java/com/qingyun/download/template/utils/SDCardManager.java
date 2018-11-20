package com.qingyun.download.template.utils;

import android.os.Environment;

import java.io.File;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class SDCardManager {
    private static SDCardManager instance;
    private File appDir = null;

    private SDCardManager() {
        //判断sd卡是否挂载
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            //获取跟目录
            appDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "QYFolder");
            appDir.mkdirs();
        }
    }

    public static SDCardManager getInstance() {
        if (instance == null) {
            synchronized (SDCardManager.class) {
                if (instance == null) {
                    instance = new SDCardManager();
                }
            }
        }
        return instance;
    }

    public File getAppDir() {
        return appDir;
    }
}
