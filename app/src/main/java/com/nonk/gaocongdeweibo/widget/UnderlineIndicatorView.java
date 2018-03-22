package com.nonk.gaocongdeweibo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.nonk.gaocongdeweibo.R;

/**
 * Created by monk on 2018/3/22.
 */

public class UnderlineIndicatorView extends LinearLayout {
    private int mCurrentPosition;

    public UnderlineIndicatorView(Context context) {
        this(context, null);
    }

    public UnderlineIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        int count = 4;
        for (int i = 0; i < count; i++) {
            View view = new View(context);
            LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            view.setLayoutParams(params);
            view.setBackgroundResource(R.color.transparent);
            addView(view);
        }
    }

    public void setCurrentItemWidthOutAnim(int position) {
        final View oldChild = getChildAt(mCurrentPosition);
        final View newChild = getChildAt(position);

        oldChild.setBackgroundResource(R.color.transparent);
        newChild.setBackgroundResource(R.color.orange);

        mCurrentPosition = position;
        invalidate();
    }

    public void setCurrentItem(int position) {
        final View oldChild = getChildAt(mCurrentPosition);
        final View newChild = getChildAt(position);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, position - mCurrentPosition,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        translateAnimation.setDuration(200);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                oldChild.setBackgroundResource(R.color.transparent);
                newChild.setBackgroundResource(R.color.orange);
            }
        });
        oldChild.setAnimation(translateAnimation);
        mCurrentPosition=position;
        invalidate();
    }

}
