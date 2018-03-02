package com.nonk.gaocongdeweibo.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nonk.gaocongdeweibo.Bean.Comment;
import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.utils.DateUtils;
import com.nonk.gaocongdeweibo.utils.StringUtils;
import com.nonk.gaocongdeweibo.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by monk on 2018/2/28.
 */

public class StatusCommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> comments;
    private ImageLoader imageLoader;

    public StatusCommentAdapter(Context context,List<Comment> comments){
        this.context=context;
        this.comments=comments;
        this.imageLoader=ImageLoader.getInstance();
    }
    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderList holderList;
        if(convertView==null){
            holderList=new ViewHolderList();
            convertView=View.inflate(context, R.layout.item_comment,null);
            holderList.ll_comments=(LinearLayout)convertView.findViewById(R.id.ll_comments);
            holderList.iv_avater=(ImageView)convertView.findViewById(R.id.iv_avater);
            holderList.tv_subhead=(TextView) convertView.findViewById(R.id.tv_subhead);
            holderList.tv_caption=(TextView)convertView.findViewById(R.id.tv_caption);
            holderList.tv_comment=(TextView)convertView.findViewById(R.id.tv_comment);
            convertView.setTag(holderList);
        }else {
            holderList=(ViewHolderList)convertView.getTag();
        }
        Comment comment=getItem(position);
        User user=comment.getUser();

        imageLoader.displayImage(user.getProfile_image_url(),holderList.iv_avater);
        holderList.tv_subhead.setText(user.getName());
        holderList.tv_caption.setText(DateUtils.getShortTime(comment.getCreated_at()));
        SpannableString weiboContent= StringUtils.getWeiboContent(context,holderList.tv_comment,comment.getText());
        holderList.tv_comment.setText(weiboContent);
        holderList.ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context,"回复评论", Toast.LENGTH_SHORT);
            }
        });
        return convertView;
    }

    public static class ViewHolderList{
        public LinearLayout ll_comments;
        public ImageView iv_avater;
        public TextView tv_subhead;
        public TextView tv_caption;
        public TextView tv_comment;
    }
}
