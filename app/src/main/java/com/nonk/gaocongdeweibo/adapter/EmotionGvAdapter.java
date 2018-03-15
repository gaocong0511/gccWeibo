package com.nonk.gaocongdeweibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.utils.EmotionUtils;

import java.util.List;

/**
 * Created by monk on 2018/3/15.
 */

public class EmotionGvAdapter extends BaseAdapter {
    private Context context;
    private List<String> emotionNames;
    private int itemWidth;

    /**
     * 构造函数
     * @param context
     * @param emotionNames
     * @param itemWidth
     */
    public EmotionGvAdapter(Context context, List<String> emotionNames, int itemWidth) {
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return emotionNames.size()+1;
    }

    @Override
    public String  getItem(int position) {
        return emotionNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * getView方法之中将表情都给填充进去
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv=new ImageView(context);
        AbsListView.LayoutParams params=new AbsListView.LayoutParams(itemWidth,itemWidth);
        iv.setPadding(itemWidth/8,itemWidth/8,itemWidth/8,itemWidth/8);
        iv.setLayoutParams(params);
        iv.setBackgroundResource(R.drawable.bg_tran2gray_sel);

        if(position==getCount()-1){
            iv.setImageResource(R.drawable.emotion_delete_icon);
        }else {
            String emotionName=getItem(position);
            iv.setImageResource(EmotionUtils.getImgByName(emotionName));
        }
        return iv;
    }
}
