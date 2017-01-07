package com.qingyun.adapter;

import android.content.Context;
import android.view.View;

import com.qingyun.download.R;
import com.qingyun.download.DownLoadRequestDao;
import com.qingyun.download.DownLoadState;
import com.qingyun.download.DownLoadUtils;
import com.qingyun.imageloader.ImageLoader;
import com.qingyun.utils.CommonUtils;

import java.util.List;

/**
 * 作者： qingyun on 17/1/6.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRecycleAdapter extends RecycleCommonAdapter<DownLoadRequestDao> {

    public DownLoadRecycleAdapter(Context context, List<DownLoadRequestDao> list) {
        super(context, list);
    }

    @Override
    protected void bindData(RecycleCommonViewHolder holder, int position, DownLoadRequestDao data) {
        if (holder.getImageView(R.id.fileIcon).getTag() == null || !holder.getImageView(R.id.fileIcon).getTag().toString().equals(data.getFileIconUrl())) {
            ImageLoader.getInstance().loadDrawable(context, holder.getImageView(R.id.fileIcon), data.getFileIconUrl());
            holder.getImageView(R.id.fileIcon).setTag(data.getFileIconUrl());
        }
        holder.getTextView(R.id.fileName).setText(data.getFileName());
        holder.getView(R.id.downLoadLayout).setOnClickListener((view) -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, holder.getLayoutPosition());
            }
        });
        if (data.getCreateTime() > 0) {
            holder.getTextView(R.id.fileTime).setText(CommonUtils.dateDifferent(data.getCreateTime()));
        }
        if (data.getDownLoadState() == DownLoadState.loading) {
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.VISIBLE);
            holder.getProgressBar(R.id.fileProgress).setProgress(data.getProgress());
            holder.getTextView(R.id.fileSize).setText(DownLoadUtils.convertStorage(data.getFileSize()) + "/");
            holder.getTextView(R.id.fileCurrentSize).setText(DownLoadUtils.convertStorage(data.getFileCurrentSize()));
            holder.getTextView(R.id.fileState).setText("下载中...");
        } else if (data.getDownLoadState() == DownLoadState.init) {
            holder.getTextView(R.id.fileState).setText("等待状态");
            if (data.getFileSize() == 0) {
                holder.getTextView(R.id.fileSize).setText("");
                holder.getTextView(R.id.fileCurrentSize).setText("");
            } else {
                holder.getTextView(R.id.fileSize).setText(DownLoadUtils.convertStorage(data.getFileSize()) + "/");
                holder.getTextView(R.id.fileCurrentSize).setText(DownLoadUtils.convertStorage(data.getFileCurrentSize()));
            }
        } else if (data.getDownLoadState() == DownLoadState.error) {
            holder.getTextView(R.id.fileState).setText("下载失败");
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
        } else if (data.getDownLoadState() == DownLoadState.stop) {
            holder.getTextView(R.id.fileState).setText("下载暂停");
        } else if (data.getDownLoadState() == DownLoadState.scuess) {
            holder.getTextView(R.id.fileState).setText("下载成功");
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_recycle_download;
    }
}