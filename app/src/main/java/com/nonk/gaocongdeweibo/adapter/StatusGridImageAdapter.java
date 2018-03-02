package com.nonk.gaocongdeweibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nonk.gaocongdeweibo.Bean.PicUrls;
import com.nonk.gaocongdeweibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by monk on 2018/1/30.
 */

public class StatusGridImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PicUrls> datas;
    ImageLoader imageLoader;

    public StatusGridImageAdapter(Context context,ArrayList<PicUrls> datas){
        this.context=context;
        this.datas=datas;
        imageLoader=ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public PicUrls getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_grid_image,null);
            holder.iv_image=(ImageView)convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        GridView gv=(GridView)parent;
        int horizontalSpacing=gv.getHorizontalSpacing();
        int numColumns=gv.getNumColumns();
        int itemWidth=(gv.getWidth()-(numColumns-1)*horizontalSpacing-gv.getPaddingLeft()-gv.getListPaddingRight())/numColumns;

        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(itemWidth,itemWidth);
        holder.iv_image.setLayoutParams(params);

        PicUrls urls=getItem(position);
        imageLoader.displayImage(urls.getThumbnail_pic(),holder.iv_image);
        return convertView;
    }

    public static class ViewHolder{
        public ImageView iv_image;
    }
}
