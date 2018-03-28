package com.nonk.gaocongdeweibo;

import android.app.Application;
import android.content.Context;

import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.utils.GccImageLoader;
import com.nonk.gaocongdeweibo.utils.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.previewlibrary.ZoomMediaLoader;


public class BaseApplication extends Application {
    /**
     *
     */
    public User currentUser;
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        ZoomMediaLoader.getInstance().init(new GccImageLoader());
    }

    private void initImageLoader(Context context){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
