package com.qingyun.imageloader;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.qingyun.download.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qingyun.utils.LogUtils;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public final class ImageLoader {
    /**
     * 圆角矩形
     */
    public static final int ImageRoundRectType = 1;
    /**
     * 圆形
     */
    public static final int ImageRoundType = 2;
    /**
     * 方形
     */
    public static final int ImageNormalType = 3;
    private static ImageLoader urlImageViewHelper = null;

    public static ImageLoader getInstance() {
        if (urlImageViewHelper == null) {
            synchronized (ImageLoader.class) {
                if (urlImageViewHelper == null) {
                    urlImageViewHelper = new ImageLoader();
                }
            }
        }
        return urlImageViewHelper;
    }

    private void showNormalTypeImageView(Context context, final ImageView imageView,
                                         final String url, final int defaultResource) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .placeholder(defaultResource)
                .dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    private void showRoundTypeImageView(Context context, final ImageView imageView,
                                        final String url, final int defaultResource) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .placeholder(defaultResource)
                .dontAnimate()
                .bitmapTransform(new RoundedCornersTransformation(Glide.get(context).getBitmapPool(), 30, 0))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    private void showCircleTypeImageView(Context context, final ImageView imageView,
                                         final String url, final int defaultResource) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .placeholder(defaultResource)
                .dontAnimate()
                .bitmapTransform(new CropCircleTransformation(Glide.get(context).getBitmapPool()))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    public void loadDrawable(Context context, final ImageView imageView,
                             final String url) {
        loadDrawable(context, imageView, url, R.drawable.default_image, ImageNormalType);
    }

    public void loadDrawable(Context context, final ImageView imageView,
                             final String url, int mImageLoadType) {
        loadDrawable(context, imageView, url, R.drawable.default_image, mImageLoadType);
    }

    public void loadDrawable(Context context, final ImageView imageView,
                             final String url, final int defaultResource, int mImageLoadType) {
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(defaultResource);
            return;
        }
        LogUtils.v("UrlDrawable", "setUrlDrawable:" + url);
        String loadUrl = convertImageUrl(url);
        if (mImageLoadType == ImageNormalType) {
            showNormalTypeImageView(context, imageView, loadUrl, defaultResource);
        } else if (mImageLoadType == ImageRoundRectType) {
            showRoundTypeImageView(context, imageView, loadUrl, defaultResource);
        } else if (mImageLoadType == ImageRoundType) {
            showCircleTypeImageView(context, imageView, loadUrl, defaultResource);
        }
    }

    public String convertImageUrl(String url) {
        if (url.startsWith("http://")) {
            return url;
        } else if (url.startsWith(Environment.getExternalStorageDirectory()
                .toString())) {
            return "file://" + url;
        }
        return url;
    }

}
