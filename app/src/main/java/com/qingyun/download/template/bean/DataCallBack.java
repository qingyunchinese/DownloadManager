package com.qingyun.download.template.bean;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import com.qingyun.download.dao.DownLoadJob;

import java.util.List;

public class DataCallBack extends DiffUtil.Callback
{
    private List<DownLoadJob> newList;
    private List<DownLoadJob> oldList;

    public DataCallBack(List<DownLoadJob> newList, List<DownLoadJob> oldList)
    {
        super();
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize()
    {
        return oldList == null ? 0 : oldList.size();
    }

    @Override
    public int getNewListSize()
    {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
    {
        DownLoadJob oldDownloadDao = oldList.get(oldItemPosition);
        DownLoadJob newDownloadDao = newList.get(newItemPosition);
        if (TextUtils.equals(oldDownloadDao.getDownLoadUrl(), newDownloadDao.getDownLoadUrl()))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
    {
        DownLoadJob oldDownloadDao = oldList.get(oldItemPosition);
        DownLoadJob newDownloadDao = newList.get(newItemPosition);
        boolean areContentsTheSame = true;
        if (oldDownloadDao.getDownLoadState() != newDownloadDao.getDownLoadState())
        {
            areContentsTheSame = false;
        }
        if (oldDownloadDao.getFileCurrentSize() != newDownloadDao.getFileCurrentSize())
        {
            areContentsTheSame = false;
        }
        if (oldDownloadDao.getSpeed() != newDownloadDao.getSpeed())
        {
            areContentsTheSame = false;
        }
        return areContentsTheSame;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition)
    {
        Bundle bundle = new Bundle();
        DownLoadJob oldDownloadDao = oldList.get(oldItemPosition);
        DownLoadJob newDownloadDao = newList.get(newItemPosition);
        if (oldDownloadDao.getSpeed() != newDownloadDao.getSpeed())
        {
            bundle.putLong("CurrentSize", newDownloadDao.getFileCurrentSize());
            bundle.putLong("Speed", newDownloadDao.getSpeed());
        }
        if (oldDownloadDao.getDownLoadState() != newDownloadDao.getDownLoadState())
        {
            bundle.putLong("DownLoadState", newDownloadDao.getDownLoadState());
        }
        if (bundle.size() == 0)
        {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
        else
        {
            return bundle;
        }
    }
}
