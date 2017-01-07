package com.qingyun.db;


import android.database.sqlite.SQLiteDatabase;

import com.qingyun.application.QYApplication;
import com.qingyun.download.DownLoadRequestDao;
import com.qingyun.greenDao.DaoMaster;
import com.qingyun.greenDao.DaoSession;
import com.qingyun.greenDao.DownLoadRequestDaoDao;
import com.qingyun.utils.LogUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 作者： qingyun on 17/1/7.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class GreenDaoUtils {
    private DaoMaster.DevOpenHelper devOpenHelper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private static GreenDaoUtils greenDaoUtils;

    private GreenDaoUtils() {
        initGreenDao();
    }

    public static GreenDaoUtils getSingleTon() {
        if (greenDaoUtils == null) {
            synchronized (GreenDaoUtils.class) {
                if (greenDaoUtils == null) {
                    greenDaoUtils = new GreenDaoUtils();
                }
            }
        }
        return greenDaoUtils;
    }

    private void initGreenDao() {
        devOpenHelper = new DaoMaster.DevOpenHelper(QYApplication.getInstacne(), "QingYunDB", null);
        db = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


    public List<DownLoadRequestDao> getAll() {
        QueryBuilder<DownLoadRequestDao> builder = daoSession.getDownLoadRequestDaoDao().queryBuilder();
        return builder.build().list();
    }

    public void deleteAll() {
        DownLoadRequestDaoDao dao = daoSession.getDownLoadRequestDaoDao();
        dao.deleteAll();
    }

    public void insert(DownLoadRequestDao downLoadRequestDao) {
        String downLoadUrl = downLoadRequestDao.getDownLoadUrl();
        QueryBuilder queryBuilder = daoSession.getDownLoadRequestDaoDao().queryBuilder();
        queryBuilder.where(DownLoadRequestDaoDao.Properties.DownLoadUrl.eq(downLoadUrl)).orderDesc(DownLoadRequestDaoDao.Properties.CreateTime);
        List<DownLoadRequestDao> downLoadRequestDaoList = queryBuilder.list();
        if (downLoadRequestDaoList.size() == 1) {
            LogUtils.v("db", "update");
            downLoadRequestDao.setCreateTime(downLoadRequestDaoList.get(0).getCreateTime());
            DownLoadRequestDaoDao dao = daoSession.getDownLoadRequestDaoDao();
            dao.update(downLoadRequestDao);
        } else {
            LogUtils.v("db", "insert");
            DownLoadRequestDaoDao dao = daoSession.getDownLoadRequestDaoDao();
            dao.insert(downLoadRequestDao);
        }
    }
}
