package com.nonk.gaocongdeweibo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nonk.gaocongdeweibo.Bean.UserItem;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.utils.ToastUtils;

import java.util.List;

/**
 * Created by monk on 2018/3/21.
 */

public class UserItemAdapter extends BaseAdapter {
    private Context context;
    private List<UserItem> datas;
    public UserItemAdapter(Context context,List<UserItem> datas){
        this.context=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public UserItem getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view ==null){
            holder=new ViewHolder();
            view=View.inflate(context, R.layout.item_user,null);
            holder.v_divider=view.findViewById(R.id.v_divider);
            holder.ll_content=view.findViewById(R.id.ll_content);
            holder.iv_left=view.findViewById(R.id.iv_left);
            holder.tv_caption=view.findViewById(R.id.tv_caption);
            holder.tv_subhead=view.findViewById(R.id.tv_subhead);
            view.setTag(holder);
        }else {
            holder=(ViewHolder) view.getTag();
        }

        UserItem item=getItem(i);
        holder.iv_left.setImageResource(item.getLeftImg());
        holder.tv_subhead.setText(item.getSubhead());
        holder.tv_caption.setText(item.getCaptoion());

        holder.v_divider.setVisibility(item.isShowTopDivider()?View.VISIBLE:View.GONE);
        holder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context,"点击位置："+i, Toast.LENGTH_SHORT);
            }
        });
        return view;
    }

    public static class ViewHolder{
        public View v_divider;
        public View ll_content;
        public ImageView iv_left;
        public TextView tv_subhead;
        public TextView tv_caption;
    }
}
