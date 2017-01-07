package com.qingyun.greenDao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.qingyun.download.DownLoadRequestDao;

import com.qingyun.greenDao.DownLoadRequestDaoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig downLoadRequestDaoDaoConfig;

    private final DownLoadRequestDaoDao downLoadRequestDaoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        downLoadRequestDaoDaoConfig = daoConfigMap.get(DownLoadRequestDaoDao.class).clone();
        downLoadRequestDaoDaoConfig.initIdentityScope(type);

        downLoadRequestDaoDao = new DownLoadRequestDaoDao(downLoadRequestDaoDaoConfig, this);

        registerDao(DownLoadRequestDao.class, downLoadRequestDaoDao);
    }
    
    public void clear() {
        downLoadRequestDaoDaoConfig.clearIdentityScope();
    }

    public DownLoadRequestDaoDao getDownLoadRequestDaoDao() {
        return downLoadRequestDaoDao;
    }

}