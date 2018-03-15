package com.nonk.gaocongdeweibo.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by monk on 2018/3/14.
 */

public class DisplayUtils {

    /**
     * 将px值转换为dp值
     * @param context
     * @param pxValue
     * @return
     */
    public  static int pxToDp(Context context,float pxValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale+0.5f);
    }

    /**
     * 将dp值转换成为px值
     * @param context
     * @param dpValue
     * @return
     */
    public static int dpToPx(Context context,float dpValue){
        final float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    /**
     * 获取屏幕的宽度
     * @param context
     * @return
     */
    public static int getScreenWidthPixels(Activity context){
        DisplayMetrics metrics=new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 获取当前屏幕的高度
     * @param context
     * @return
     */
    public static int getScreenHeightPixels(Activity context){
        DisplayMetrics metrics=new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}
