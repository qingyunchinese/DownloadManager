package com.qingyun.download.template.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.qingyun.download.QYDownLoadManager;
import com.qingyun.download.db.DBHelper;

/**
 * Created by qingyun1 on 16/11/22.
 */

public class QYApplication extends Application {
    private static QYApplication instacne;

    @Override
    public void onCreate() {
        super.onCreate();
        instacne = this;
        QYDownLoadManager.init(this);
    }

    public static QYApplication getInstacne() {
        return instacne;
    }


    public void exitApp(Context context) {
        try {
            closeDb();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName()
                    + ":downLoadService");
            activityMgr.killBackgroundProcesses(context.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDb() {
        try {
            DBHelper db = DBHelper.getInstance(this);
            db.closeDb();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
