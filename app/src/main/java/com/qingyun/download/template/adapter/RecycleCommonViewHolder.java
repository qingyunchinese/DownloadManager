package com.qingyun.download.template.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 作者： qingyun on 16/11/26.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 */
public class RecycleCommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> viewSparseArray;
    private Context context;

    public RecycleCommonViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        viewSparseArray = new SparseArray<>();

    }

    private <T extends View> T findViewById(int viewId) {
        View view = viewSparseArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public View getView(int viewId) {
        return findViewById(viewId);
    }

    public ViewStub getViewStub(int viewId) {
        return findViewById(viewId);
    }

    public TextView getTextView(int viewId) {
        return (TextView) getView(viewId);
    }

    public ProgressBar getProgressBar(int viewId) {
        return (ProgressBar) getView(viewId);
    }

    public ImageView getImageView(int viewId) {
        return (ImageView) getView(viewId);
    }

    public RecycleCommonViewHolder setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        view.setText(value);
        return this;
    }

    public RecycleCommonViewHolder setClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}
