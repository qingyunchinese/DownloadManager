package com.qingyun.download.template.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.qingyun.download.DownLoadManager;


public class QYApplication extends Application {
    private static QYApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DownLoadManager.init();
    }

    public static QYApplication getInstance() {
        return instance;
    }


    public void exitApp(Context context)
    {
        try
        {
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
