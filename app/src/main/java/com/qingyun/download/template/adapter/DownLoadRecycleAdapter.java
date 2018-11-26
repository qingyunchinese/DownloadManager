package com.qingyun.download.template.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import com.qingyun.download.DownLoadState;
import com.qingyun.download.dao.DownLoadJob;
import com.qingyun.download.utils.DownLoadUtils;
import com.qingyun.download.template.R;
import com.qingyun.download.template.imageloader.ImageLoader;

import java.util.List;

/**
 * 作者： qingyun on 17/1/6.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadRecycleAdapter extends RecycleCommonAdapter<DownLoadJob>
{
    private OnDownLoadItemViewClick onDownLoadItemViewClick;

    public DownLoadRecycleAdapter(Context context, List<DownLoadJob> list)
    {
        super(context, list);
    }

    @Override
    protected void bindData(final RecycleCommonViewHolder holder,final int position, final DownLoadJob data)
    {
        ImageLoader.getInstance().loadDrawable(context, holder.getImageView(R.id.fileIcon), data.getFileIconUrl());
        holder.getTextView(R.id.fileName).setText(data.getFileName());
        holder.getView(R.id.downLoadLayout).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.getView(R.id.stopDownLoad).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onDownLoadItemViewClick != null)
                {
                    onDownLoadItemViewClick.onStopClick(data);
                }
            }
        });
        holder.getTextView(R.id.fileSpeed).setText("");
        refreshDynamicView(holder, data);
    }

    private void refreshDynamicView(RecycleCommonViewHolder holder, DownLoadJob data)
    {
        if (data.getDownLoadState() == DownLoadState.loading)
        {
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.VISIBLE);
            holder.getProgressBar(R.id.fileProgress).setProgress(data.getProgress());
            holder.getTextView(R.id.fileSize).setText(DownLoadUtils.convertStorage(data.getFileSize()) + "/");
            holder.getTextView(R.id.fileCurrentSize).setText(DownLoadUtils.convertStorage(data.getFileCurrentSize()));
            holder.getTextView(R.id.fileState).setText("下载中...");
            if (data.getSpeed() > 0)
            {
                holder.getTextView(R.id.fileSpeed).setText(DownLoadUtils.convertStorage(data.getSpeed()) + "/S");
            }
            else
            {
                holder.getTextView(R.id.fileSpeed).setText("0B/S");
            }
        }
        else if (data.getDownLoadState() == DownLoadState.init)
        {
            holder.getTextView(R.id.fileState).setText("等待状态");
            if (data.getFileSize() == 0)
            {
                holder.getTextView(R.id.fileSize).setText("");
                holder.getTextView(R.id.fileCurrentSize).setText("");
            }
            else
            {
                holder.getTextView(R.id.fileSize).setText(DownLoadUtils.convertStorage(data.getFileSize()) + "/");
                holder.getTextView(R.id.fileCurrentSize).setText(DownLoadUtils.convertStorage(data.getFileCurrentSize()));
            }
        }
        else if (data.getDownLoadState() == DownLoadState.error)
        {
            holder.getTextView(R.id.fileState).setText("下载失败");
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
        }
        else if (data.getDownLoadState() == DownLoadState.stop)
        {
            holder.getTextView(R.id.fileState).setText("下载暂停");
        }
        else if (data.getDownLoadState() == DownLoadState.success)
        {
            holder.getTextView(R.id.fileState).setText("下载成功");
            holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected int getItemLayoutId(int viewType)
    {
        return R.layout.adapter_recycle_download;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleCommonViewHolder holder, int position, @NonNull List payloads)
    {
        if (payloads.isEmpty())
        {
            super.onBindViewHolder(holder, position, payloads);
            return;
        }
        Bundle payload = (Bundle) payloads.get(0);
        DownLoadJob downLoadJob = getCommonDataList().get(position);
        for (String key : payload.keySet())
        {
            switch (key)
            {
                case "Speed":
                    if (downLoadJob.getSpeed() > 0)
                    {
                        holder.getTextView(R.id.fileSpeed).setText(DownLoadUtils.convertStorage(downLoadJob.getSpeed()) + "/S");
                    }
                    else
                    {
                        holder.getTextView(R.id.fileSpeed).setText("0B/S");
                    }
                    break;
                case "CurrentSize":
                    holder.getProgressBar(R.id.fileProgress).setVisibility(View.VISIBLE);
                    holder.getProgressBar(R.id.fileProgress).setProgress(downLoadJob.getProgress());
                    holder.getTextView(R.id.fileSize).setText(DownLoadUtils.convertStorage(downLoadJob.getFileSize()) + "/");
                    holder.getTextView(R.id.fileCurrentSize).setText(DownLoadUtils.convertStorage(downLoadJob.getFileCurrentSize()));
                    break;
                case "DownLoadState":
                    if (downLoadJob.getDownLoadState() == DownLoadState.loading)
                    {
                        holder.getTextView(R.id.stopDownLoad).setText("暂停");
                        holder.getTextView(R.id.fileState).setText("下载中...");
                    }
                    else if (downLoadJob.getDownLoadState() == DownLoadState.init)
                    {
                        holder.getTextView(R.id.stopDownLoad).setText("开始");
                        holder.getTextView(R.id.fileState).setText("等待状态");
                    }
                    else if (downLoadJob.getDownLoadState() == DownLoadState.error)
                    {
                        holder.getTextView(R.id.stopDownLoad).setText("开始");
                        holder.getTextView(R.id.fileState).setText("下载失败");
                        holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
                    }
                    else if (downLoadJob.getDownLoadState() == DownLoadState.stop)
                    {
                        holder.getTextView(R.id.stopDownLoad).setText("开始");
                        holder.getTextView(R.id.fileState).setText("下载暂停");
                    }
                    else if (downLoadJob.getDownLoadState() == DownLoadState.success)
                    {
                        holder.getTextView(R.id.stopDownLoad).setText("开始");
                        holder.getTextView(R.id.fileState).setText("下载成功");
                        holder.getProgressBar(R.id.fileProgress).setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setOnDownLoadItemViewClick(OnDownLoadItemViewClick onDownLoadItemViewClick)
    {
        this.onDownLoadItemViewClick = onDownLoadItemViewClick;
    }

    public interface OnDownLoadItemViewClick
    {
        void onStopClick(DownLoadJob downLoadJob);
    }
}