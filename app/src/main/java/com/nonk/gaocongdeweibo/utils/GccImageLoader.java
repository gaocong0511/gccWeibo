package com.nonk.gaocongdeweibo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.nonk.gaocongdeweibo.R;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

/**
 * Created by monk on 2018/3/27.
 */

public class GccImageLoader implements IZoomMediaLoader {
    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, @NonNull final MySimpleTarget<Bitmap> simpleTarget) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_launcher)// 正在加载中的图片
                .error(R.drawable.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL);  // 加载失败的图片

        Glide.with(context).applyDefaultRequestOptions(options).load(path).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                BitmapDrawable bd = (BitmapDrawable) resource;
                Bitmap bm = bd.getBitmap();
                simpleTarget.onResourceReady(bm);
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                simpleTarget.onLoadStarted();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                simpleTarget.onLoadFailed(errorDrawable);
            }
        });

    }


    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
    }

    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}
