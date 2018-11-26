package com.qingyun.download.template.application;

import android.Manifest.permission;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.DiffUtil.DiffResult;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qingyun.download.DownLoadManager;
import com.qingyun.download.dao.DownLoadJob;
import com.qingyun.download.template.adapter.DownLoadRecycleAdapter;
import com.qingyun.download.template.adapter.DownLoadRecycleAdapter.OnDownLoadItemViewClick;
import com.qingyun.download.template.adapter.RecycleCommonAdapter;
import com.qingyun.download.DownLoadState;
import com.qingyun.download.DownloadListener;
import com.qingyun.download.template.bean.DataCallBack;
import com.qingyun.download.template.utils.CommonUtils;
import com.qingyun.download.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.qingyun.download.template.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：下载测试类
 */
public class DownLoadFileActivity extends BaseActivity implements View.OnClickListener, DownloadListener, RecycleCommonAdapter.OnItemClickListener, OnDownLoadItemViewClick
{

    private String[] requestPermissions = {permission.ACCESS_NETWORK_STATE, permission.WRITE_EXTERNAL_STORAGE, permission.KILL_BACKGROUND_PROCESSES
            , permission.READ_EXTERNAL_STORAGE};
    public static final int APP_DEFAULT_REQUEST_PERMISSIONS = 82;
    private static final String TAG = "DownLoadFileActivity";
    @BindView(com.qingyun.download.template.R.id.recycleView)
    RecyclerView recycleView;
    LinearLayoutManager layoutManager;
    @BindView(R.id.startDownLoad)
    Button newDownLoad;
    @BindView(R.id.stopDownLoad)
    Button stopDownLoad;
    private DownLoadRecycleAdapter downLoadRecycleAdapter;
    private List<DownLoadJob> oldDownLoadJobList = new ArrayList<>();
    private List<DownLoadJob> newDownLoadJobList = new ArrayList<>();
    private Set<String> downLoadUrlSet = new HashSet<>();
    private long exitTime = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
        ButterKnife.bind(this);

        mHandler = new Handler();
        getSupportActionBar().setTitle("下载");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        bindClick();
        initRecyclerView();
        initCacheData();
        refreshRecycleView();

        if (!checkAppPermissions(requestPermissions))
        {
            requestAppPermissions(requestPermissions, APP_DEFAULT_REQUEST_PERMISSIONS);
        }
        else
        {
            onRequestPermissionSuccess();
        }
    }

    @Override
    protected void onRequestPermissionSuccess()
    {
        DownLoadManager.getInstance().registerDownLoadListener(this);
    }

    private void bindClick()
    {
        stopDownLoad.setOnClickListener(this);
        newDownLoad.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        DownLoadManager.getInstance().unregisterDownLoadListener(this);
    }

    private void initRecyclerView()
    {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.addItemDecoration(dividerItemDecoration);
        recycleView.setLayoutManager(layoutManager);
        downLoadRecycleAdapter = new DownLoadRecycleAdapter(this, newDownLoadJobList);
        downLoadRecycleAdapter.setOnItemClickListener(this);
        downLoadRecycleAdapter.setOnDownLoadItemViewClick(this);
        recycleView.setAdapter(downLoadRecycleAdapter);
    }

    private void onClick(DownLoadJob downLoadJob)
    {
        if (downLoadJob.getDownLoadState() == DownLoadState.loading || downLoadJob.getDownLoadState() == DownLoadState.start)
        {
            LogUtil.v(TAG,"暂停下载");
            DownLoadManager.getInstance().stopDownLoadFile(DownLoadFileActivity.this, downLoadJob.getDownLoadUrl());
        }
        else if (downLoadJob.getDownLoadState() == DownLoadState.error || downLoadJob.getDownLoadState() == DownLoadState.stop || downLoadJob.getDownLoadState() == DownLoadState.init)
        {
            LogUtil.v(TAG,"重新的下载");
            DownLoadManager.getInstance().downLoadFile(DownLoadFileActivity.this, downLoadJob);
        }
        else if (downLoadJob.getDownLoadState() == DownLoadState.success)
        {
            Toast.makeText(this, "已下载完成", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownLoadFailure(String url, String cachePath, String strMsg)
    {
        LogUtil.v(TAG, "onDownLoadFailure");
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJobError(url, strMsg);
        refreshRecycleView();
    }

    @Override
    public void onDownLoadLoading(String url, String cachePath, long count, long current, long speed)
    {
        LogUtil.v(TAG, "onDownLoadLoading speed:" + speed + " count：" + count + " current" + current);
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJobLoading(url, count, current, speed);
        refreshRecycleView();
    }

    @Override
    public void onDownLoadStart(String url, String cachePath)
    {
        LogUtil.v(TAG, "onDownLoadStart");
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJob(url, DownLoadState.start);
        refreshRecycleView();
    }

    @Override
    public void onDownLoadSuccess(String url, String cachePath)
    {
        LogUtil.v(TAG, "onDownLoadSuccess");
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJob(url, DownLoadState.success);
        refreshRecycleView();
    }

    @Override
    public void onDownLoadStop(String url, String cachePath)
    {
        LogUtil.v(TAG, "onDownLoadStop");
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJob(url, DownLoadState.stop);
        refreshRecycleView();
    }

    @Override
    public void onDownLoadFinish(String url, String cacheFilePath)
    {
        LogUtil.v(TAG, "onDownLoadFinish:");
        if (!downLoadUrlSet.contains(url))
        {
            return;
        }
        setDownloadJob(url, DownLoadState.finish);
        refreshRecycleView();
    }

    @Override
    public void onItemClick(View itemView, int pos)
    {
        onClick(newDownLoadJobList.get(pos));
    }

    /**
     * 列表局部刷新
     */
    public synchronized void refreshRecycleView()
    {
        try
        {
            mHandler.postDelayed(freshRunnable, 400);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Runnable freshRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            LogUtil.v(TAG, "refreshRecycleView");
            List<DownLoadJob> newList = newDownLoadJobList;
            List<DownLoadJob> oldList = oldDownLoadJobList;
            DataCallBack dataCallBack = new DataCallBack(newList, oldList);
            DiffResult diffResult = DiffUtil.calculateDiff(dataCallBack);
            synchronizedDownLoadList();
            downLoadRecycleAdapter.setCommonDataList(newDownLoadJobList);
            diffResult.dispatchUpdatesTo(downLoadRecycleAdapter);
        }
    };

    private synchronized void synchronizedDownLoadList()
    {
        for (int i = 0; i < 1; i++)
        {
            DownLoadJob oldDownloadDao = oldDownLoadJobList.get(i);
            DownLoadJob newDownloadDao = newDownLoadJobList.get(i);
            oldDownloadDao.setDownLoadState(newDownloadDao.getDownLoadState());
            oldDownloadDao.setFileCurrentSize(newDownloadDao.getFileCurrentSize());
            oldDownloadDao.setFileSize(newDownloadDao.getFileSize());
            oldDownloadDao.setErrorMessage(newDownloadDao.getErrorMessage());
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.startDownLoad:
                for (DownLoadJob DownLoadJob : newDownLoadJobList)
                {
                    DownLoadManager.getInstance().downLoadFile(this, DownLoadJob);
                }
                break;
            case R.id.stopDownLoad:
                DownLoadManager.getInstance().stopAllDownLoadFile(this);
                break;
            default:
        }
    }


    private void initCacheData()
    {
        String downLoadUrl = "http://g.shouji.360tpcdn.com/161221/ddacfd095e82df4d2c5b3b029a4843aa/com.yunchang.djsy.hyjs.qihu_500.apk";
        String downLoadName = "黑衣剑士-刀剑神域动漫";
        String downLoadIcon = "http://p17.qhimg.com/t01453f12810e8c2903.png";
        newDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        oldDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        downLoadUrlSet.add(downLoadUrl);
        downLoadUrl = "http://g.shouji.360tpcdn.com/161220/9158196215e4e8f55cd400fa654a2a8f/com.game.shns.a360_40.apk";
        downLoadName = "圣斗士星矢：重生-30周年";
        downLoadIcon = "http://p19.qhimg.com/t019e543b20bcaba038.png";
        newDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        oldDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        downLoadUrlSet.add(downLoadUrl);
        downLoadUrl = "http://shouji.360tpcdn.com/170105/a8246ce9ebca8af55fb623e7311d16dd/com.miHoYo.bh3.qihoo_13.apk";
        downLoadName = "崩坏3-诅咒之剑";
        downLoadIcon = "http://p15.qhimg.com/t01327bceff77cb0e79.png";
        newDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        oldDownLoadJobList.add(createDownLoadJob(downLoadUrl, downLoadName, downLoadIcon));
        downLoadUrlSet.add(downLoadUrl);
    }

    private void setDownloadJob(String downLoadUrl, int downloadState)
    {
        for (DownLoadJob downLoadJob : newDownLoadJobList)
        {
            if (TextUtils.equals(downLoadJob.getDownLoadUrl(), downLoadUrl))
            {
                downLoadJob.setDownLoadState(downloadState);
            }
        }
    }

    private void setDownloadJobError(String downLoadUrl, String errorMsg)
    {
        for (DownLoadJob downLoadJob : newDownLoadJobList)
        {
            if (TextUtils.equals(downLoadJob.getDownLoadUrl(), downLoadUrl))
            {
                downLoadJob.setDownLoadState(DownLoadState.error);
                downLoadJob.setErrorMessage(errorMsg);
            }
        }
    }

    private void setDownloadJobLoading(String downLoadUrl, long count, long current, long speed)
    {
        for (DownLoadJob downLoadJob : newDownLoadJobList)
        {
            if (TextUtils.equals(downLoadJob.getDownLoadUrl(), downLoadUrl))
            {
                downLoadJob.setDownLoadState(DownLoadState.loading);
                downLoadJob.setFileCurrentSize(current);
                downLoadJob.setFileSize(count);
                downLoadJob.setSpeed(speed);
            }
        }
    }

    private DownLoadJob createDownLoadJob(String downLoadUrl, String downLoadName, String downLoadIcon)
    {
        DownLoadJob downLoadJob = new DownLoadJob(downLoadUrl, CommonUtils.getDownLoadDefaultPath(downLoadUrl));
        downLoadJob.setFileName(downLoadName);
        downLoadJob.setFileIconUrl(downLoadIcon);
        return downLoadJob;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                QYApplication.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStopClick(DownLoadJob downLoadJob)
    {
        onClick(downLoadJob);
    }
}
