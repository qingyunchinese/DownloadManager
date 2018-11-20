package com.qingyun.download.template.application;

import android.Manifest.permission;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qingyun.download.template.adapter.DownLoadRecycleAdapter;
import com.qingyun.download.template.adapter.RecycleCommonAdapter;
import com.qingyun.download.DownLoadRequestDao;
import com.qingyun.download.DownLoadState;
import com.qingyun.download.db.GreenDaoUtils;
import com.qingyun.download.template.utils.CommonUtils;
import com.qingyun.download.utils.DownLoadUtils;
import com.qingyun.download.DownloadListener;
import com.qingyun.download.QYDownLoadManager;
import com.qingyun.download.utils.LogUtils;
import com.qingyun.download.template.utils.SDCardManager;

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
public class DownLoadFileActivity extends BaseActivity implements View.OnClickListener, DownloadListener, RecycleCommonAdapter.OnItemClickListener {

    private String[] requestPermissions = {permission.ACCESS_NETWORK_STATE, permission.WRITE_EXTERNAL_STORAGE, permission.KILL_BACKGROUND_PROCESSES
            , permission.READ_EXTERNAL_STORAGE};
    public static final int APP_DEFAULT_REQUEST_PERMISSIONS = 82;

    private static final String TAG = "DownLoadFileActivity";
    @BindView(com.qingyun.download.template.R.id.recycleView)
    RecyclerView recycleView;
    DownLoadRecycleAdapter downLoadRecycleAdapter;
    LinearLayoutManager layoutManager;
    @BindView(R.id.newDownLoad)
    Button newDownLoad;
    @BindView(R.id.deleteDownLoadFile)
    Button deleteDownLoadFile;
    @BindView(R.id.deleteDownLoadDB)
    Button deleteDownLoadDB;
    /**用于测试的下载任务*/
    private List<DownLoadRequestDao> cacheQueueList = new ArrayList<>();
    /**缓存当前页面所需要显示的下载任务*/
    private List<DownLoadRequestDao> downLoadRequestDaoList = new ArrayList<>();
    /**主要来区分是否是本页面的下载任务*/
    private Set<String> downLoadUrlSet = new HashSet<>();
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_file);
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
        ButterKnife.bind(this);
        configDownLoadList();
        configRecyceleView();
        QYDownLoadManager.getInstance().registerDownLaodListener(this);
        bindClick();
        initCacheData();
    }

    private void bindClick() {
        newDownLoad.setOnClickListener(this);
        deleteDownLoadFile.setOnClickListener(this);
        deleteDownLoadDB.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QYDownLoadManager.getInstance().unregisterDownLaodListener(this);
    }

    private void configDownLoadList() {
        downLoadRequestDaoList = QYDownLoadManager.getInstance().initHistoryData();
        for (DownLoadRequestDao dao : downLoadRequestDaoList) {
            downLoadUrlSet.add(dao.getDownLoadUrl());
        }
    }

    private void configRecyceleView() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        ((DefaultItemAnimator) recycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        recycleView.addItemDecoration(dividerItemDecoration);
        recycleView.setLayoutManager(layoutManager);
        downLoadRecycleAdapter = new DownLoadRecycleAdapter(this, downLoadRequestDaoList);
        downLoadRecycleAdapter.setOnItemClickListener(this);
        recycleView.setAdapter(downLoadRecycleAdapter);
    }

    private void onClick(DownLoadRequestDao downLoadRequestDao) {
        if (downLoadRequestDao == null) {
            LogUtils.v("新的下载");
            QYDownLoadManager.getInstance().downLoadFile(DownLoadFileActivity.this, downLoadRequestDao);
        } else {
            if (downLoadRequestDao.getDownLoadState() == DownLoadState.loading || downLoadRequestDao.getDownLoadState() == DownLoadState.start) {
                LogUtils.v("暂停下载");
                QYDownLoadManager.getInstance().stopDownLoadFile(DownLoadFileActivity.this, downLoadRequestDao.getDownLoadUrl());
            } else if (downLoadRequestDao.getDownLoadState() == DownLoadState.error || downLoadRequestDao.getDownLoadState() == DownLoadState.stop || downLoadRequestDao.getDownLoadState() == DownLoadState.init) {
                LogUtils.v("重新的下载");
                QYDownLoadManager.getInstance().downLoadFile(DownLoadFileActivity.this, downLoadRequestDao);
            } else if (downLoadRequestDao.getDownLoadState() == DownLoadState.scuess) {
                Toast.makeText(this, "已下载完成", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDownLoadFailure(String url, String cachePath, String strMsg) {
        LogUtils.v(TAG, "onDownLoadFailure");
        if (!downLoadUrlSet.contains(url)) {
            return;
        }
        refushRecycleView();
    }

    @Override
    public void onDownLoadLoading(String url, String cachePath, long count, long current, long speed) {
        LogUtils.v(TAG, "onDownLoadLoading:"+speed);
        if (!downLoadUrlSet.contains(url)) {
            return;
        }
        refushRecycleView();
    }

    @Override
    public void onDownLoadStart(String url, String cachePath) {
        LogUtils.v(TAG, "onDownLoadStart");
        if (!downLoadUrlSet.contains(url)) {
            return;
        }
        refushRecycleView();
    }

    @Override
    public void onDownLoadSuccess(String url, String cachePath) {
        LogUtils.v(TAG, "onDownLoadSuccess");
        if (!downLoadUrlSet.contains(url)) {
            return;
        }
        refushRecycleView();
    }

    @Override
    public void onDownLoadStop(String url, String cachePath) {
        LogUtils.v(TAG, "onDownLoadStop");
        if (!downLoadUrlSet.contains(url)) {
            return;
        }
        refushRecycleView();
    }

    @Override
    public void onItemClick(View itemView, int pos) {
        onClick(downLoadRequestDaoList.get(pos));
    }


    /**
     * 列表局部刷新
     */
    public void refushRecycleView() {
        LogUtils.v(TAG, "refushRecycleView");
        int start = layoutManager.findFirstVisibleItemPosition();
        int end = layoutManager.findLastVisibleItemPosition();
        downLoadRecycleAdapter.notifyItemRangeChanged(start, end + 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newDownLoad:
                int count = downLoadRecycleAdapter.getItemCount();
                if (count < 3) {
                    DownLoadRequestDao dao = cacheQueueList.get(count);
                    downLoadRequestDaoList.add(dao);
                    downLoadUrlSet.add(dao.getDownLoadUrl());
                    downLoadRecycleAdapter.setCommonDataList(downLoadRequestDaoList);
                    downLoadRecycleAdapter.notifyItemInserted(count);
                    refushRecycleView();
                    onClick(dao);
                }else{
                    Toast.makeText(this, "默认三个下载任务,更多请自己添加", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.deleteDownLoadFile:
                QYDownLoadManager.getInstance().stopAllDownLoadFile(this);
                DownLoadUtils.deleteDir(SDCardManager.getInstance().getAppDir());
                break;
            case R.id.deleteDownLoadDB:
                GreenDaoUtils.getSingleTon().deleteAll();
                downLoadRequestDaoList.clear();
                downLoadUrlSet.clear();
                downLoadRecycleAdapter.notifyDataSetChanged();
                break;
        }
    }


    private void initCacheData() {
        String downLoadUrl = "http://g.shouji.360tpcdn.com/161221/ddacfd095e82df4d2c5b3b029a4843aa/com.yunchang.djsy.hyjs.qihu_500.apk";
        DownLoadRequestDao downLoadRequestDao1 = new DownLoadRequestDao();
        downLoadRequestDao1.setDownLoadUrl(downLoadUrl);
        downLoadRequestDao1.setCacheFilePath(CommonUtils.getDownLoadDefaultPath(downLoadUrl));
        downLoadRequestDao1.setFileName("黑衣剑士-刀剑神域动漫");
        downLoadRequestDao1.setFileIconUrl("http://p17.qhimg.com/t01453f12810e8c2903.png");
        if (DownLoadUtils.isFileExist(downLoadRequestDao1.getCacheFilePath())) {
            downLoadRequestDao1.setDownLoadState(DownLoadState.scuess);
        } else {
            downLoadRequestDao1.setDownLoadState(DownLoadState.init);
        }
        cacheQueueList.add(downLoadRequestDao1);
        downLoadUrl = "http://g.shouji.360tpcdn.com/161220/9158196215e4e8f55cd400fa654a2a8f/com.game.shns.a360_40.apk";
        DownLoadRequestDao downLoadRequestDao2 = new DownLoadRequestDao();
        downLoadRequestDao2.setDownLoadUrl(downLoadUrl);
        downLoadRequestDao2.setCacheFilePath(CommonUtils.getDownLoadDefaultPath(downLoadUrl));
        downLoadRequestDao2.setFileName("圣斗士星矢：重生-30周年");
        downLoadRequestDao2.setFileIconUrl("http://p19.qhimg.com/t019e543b20bcaba038.png");
        if (DownLoadUtils.isFileExist(downLoadRequestDao2.getCacheFilePath())) {
            downLoadRequestDao2.setDownLoadState(DownLoadState.scuess);
        } else {
            downLoadRequestDao2.setDownLoadState(DownLoadState.init);
        }
        cacheQueueList.add(downLoadRequestDao2);
        downLoadUrl = "http://shouji.360tpcdn.com/170105/a8246ce9ebca8af55fb623e7311d16dd/com.miHoYo.bh3.qihoo_13.apk";
        DownLoadRequestDao downLoadRequestDao3 = new DownLoadRequestDao();
        downLoadRequestDao3.setDownLoadUrl(downLoadUrl);
        downLoadRequestDao3.setCacheFilePath(CommonUtils.getDownLoadDefaultPath(downLoadUrl));
        downLoadRequestDao3.setFileName("崩坏3-诅咒之剑");
        downLoadRequestDao3.setFileIconUrl("http://p15.qhimg.com/t01327bceff77cb0e79.png");
        if (DownLoadUtils.isFileExist(downLoadRequestDao3.getCacheFilePath())) {
            downLoadRequestDao3.setDownLoadState(DownLoadState.scuess);
        } else {
            downLoadRequestDao3.setDownLoadState(DownLoadState.init);
        }
        cacheQueueList.add(downLoadRequestDao3);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                QYApplication.getInstacne().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
