package com.qingyun.download.template.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 作者： qingyun on 16/11/22.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public abstract class RecycleCommonAdapter<T> extends RecyclerView.Adapter<RecycleCommonViewHolder>
{
    private LayoutInflater layoutInflater;
    protected Context context;
    private List<T> commonDataList;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    public RecycleCommonAdapter(Context context, List<T> list)
    {
        this.context = context;
        this.commonDataList = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecycleCommonViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        final RecycleCommonViewHolder holder = new RecycleCommonViewHolder(context,
                layoutInflater.inflate(getItemLayoutId(viewType), parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleCommonViewHolder holder, int position)
    {
        bindData(holder, position, commonDataList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return commonDataList.size();
    }

    protected abstract void bindData(RecycleCommonViewHolder holder, int position, T data);

    protected abstract int getItemLayoutId(int viewType);

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCommonDataList(List<T> commonDataList)
    {
        this.commonDataList = commonDataList;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener)
    {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    public interface OnItemClickListener
    {
        public void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener
    {
        public void onItemLongClick(View itemView, int pos);
    }


    public List<T> getCommonDataList()
    {
        return commonDataList;
    }
}
