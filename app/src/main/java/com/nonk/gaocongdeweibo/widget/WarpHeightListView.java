package com.nonk.gaocongdeweibo.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by monk on 2018/3/20.
 */

public class WarpHeightListView extends ListView{
    public WarpHeightListView(Context context) {
        super(context);
    }

    public WarpHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WarpHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WarpHeightListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if(getLayoutParams().height==LayoutParams.WRAP_CONTENT){
            heightSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        }else {
            heightSpec=heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
