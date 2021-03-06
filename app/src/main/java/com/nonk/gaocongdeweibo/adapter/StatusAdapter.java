package com.nonk.gaocongdeweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nonk.gaocongdeweibo.Activity.MainActivity;
import com.nonk.gaocongdeweibo.Activity.StatusDetailActivity;
import com.nonk.gaocongdeweibo.Activity.UserInfoActivity;
import com.nonk.gaocongdeweibo.Activity.WriteCommentActivity;
import com.nonk.gaocongdeweibo.Activity.WriteStatusActivity;
import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.PhotoViewInfo;
import com.nonk.gaocongdeweibo.Bean.PicUrls;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.utils.DateUtils;
import com.nonk.gaocongdeweibo.utils.StringUtils;
import com.nonk.gaocongdeweibo.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.previewlibrary.GPreviewBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monk on 2018/1/29.
 */

public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<Status> datas;
    private ImageLoader imageLoader;

    public StatusAdapter(Context context, List<Status> datas) {
        this.context = context;
        this.datas = datas;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Status getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_status, null);
            holder.ll_card_content = (LinearLayout) convertView
                    .findViewById(R.id.ll_card_content);
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avater);
            holder.rl_content = (RelativeLayout) convertView
                    .findViewById(R.id.rl_content);
            holder.tv_subhead = (TextView) convertView
                    .findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView
                    .findViewById(R.id.tv_caption);

            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.include_status_image = (FrameLayout) convertView
                    .findViewById(R.id.include_status_image);
            holder.gv_images = (GridView) holder.include_status_image
                    .findViewById(R.id.gv_images);
            holder.iv_image = (ImageView) holder.include_status_image
                    .findViewById(R.id.iv_image);

            holder.include_retweeted_status = (LinearLayout) convertView
                    .findViewById(R.id.include_retweeted_status);
            holder.tv_retweeted_content = (TextView) holder.include_retweeted_status
                    .findViewById(R.id.tv_retweeted_content);
            holder.include_retweeted_status_image = (FrameLayout) holder.include_retweeted_status
                    .findViewById(R.id.include_status_image);
            holder.gv_retweeted_images = (GridView) holder.include_retweeted_status_image
                    .findViewById(R.id.gv_images);
            holder.iv_retweeted_image = (ImageView) holder.include_retweeted_status_image
                    .findViewById(R.id.iv_image);

            holder.ll_share_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_share_bottom);
            holder.iv_share_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_share_bottom);
            holder.tv_share_bottom = (TextView) convertView
                    .findViewById(R.id.tv_share_bottom);
            holder.ll_comment_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_comment_bottom);
            holder.iv_comment_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_comment_bottom);
            holder.tv_comment_bottom = (TextView) convertView
                    .findViewById(R.id.tv_comment_bottom);
            holder.ll_like_bottom = (LinearLayout) convertView
                    .findViewById(R.id.ll_like_bottom);
            holder.iv_like_bottom = (ImageView) convertView
                    .findViewById(R.id.iv_like_bottom);
            holder.tv_like_bottom = (TextView) convertView
                    .findViewById(R.id.tv_like_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //给用户来绑定数据
        final Status status = (Status) getItem(position);
        final User user = status.getUser();
        imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("username", user.getName());
                context.startActivity(intent);
            }
        });
        holder.tv_subhead.setText(user.getName());
        if (status.getSource().isEmpty()) {
            holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()));
        } else {
            holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) + " 来自 " + Html.fromHtml(status.getSource()));
        }

        //微博正文---待细化处理
        holder.tv_content.setText(StringUtils.getWeiboContent(context, holder.tv_content, status.getText()));
        setImages(status, holder.include_status_image, holder.gv_images, holder.iv_image);

        Status retweeted_status = status.getRetweeted_status();
        if (retweeted_status != null) {
            User retUser = retweeted_status.getUser();
            holder.include_retweeted_status.setVisibility(View.VISIBLE);
            String rewteetedContent = "@" + retUser.getName() + ":" + retweeted_status.getText();
            holder.tv_retweeted_content.setText(StringUtils.getWeiboContent(context, holder.tv_retweeted_content, rewteetedContent));

            setImages(retweeted_status, holder.include_retweeted_status_image, holder.gv_retweeted_images, holder.iv_retweeted_image);
            ArrayList<PicUrls> pic_urls = retweeted_status.getPic_urls();
            final ArrayList<PhotoViewInfo> urls = new ArrayList<>();
            for (PicUrls url : pic_urls) {
                urls.add(new PhotoViewInfo(url.getOriginal_pic()));
            }
            if (urls.size() == 1) {
                holder.iv_retweeted_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentActivity activity = (FragmentActivity) context;
                        GPreviewBuilder.from(activity)
                                .setData(urls)
                                .setCurrentIndex(0)
                                .setType(GPreviewBuilder.IndicatorType.Dot)
                                .start();
                    }
                });
            } else {
                holder.gv_retweeted_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                        //ToastUtils.showToast(context,"index"+index, Toast.LENGTH_SHORT);
                        FragmentActivity activity = (FragmentActivity) context;
                        GPreviewBuilder.from(activity)
                                .setData(urls)
                                .setCurrentIndex(index)
                                .setType(GPreviewBuilder.IndicatorType.Dot)
                                .start();
                    }
                });
            }
        } else {
            holder.include_retweeted_status.setVisibility(View.GONE);
        }
        holder.tv_share_bottom.setText(status.getReposts_count() == 0 ? "转发" : status.getReposts_count() + "");
        holder.tv_comment_bottom.setText(status.getReposts_count() == 0 ? "评论" : status.getComments_count() + "");
        holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ? "赞" : status.getAttitudes_count() + "");
        //点击转发时
        holder.ll_share_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WriteStatusActivity.class);
                intent.putExtra("status", status);
                context.startActivity(intent);
            }
        });
        //点击评论时
        holder.ll_card_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.getComments_count() > 0) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra("status", status);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, WriteCommentActivity.class);
                    intent.putExtra("status", status);
                    context.startActivity(intent);
                }
            }
        });

        ArrayList<PicUrls> pic_urls = status.getPic_urls();
        final ArrayList<PhotoViewInfo> urls = new ArrayList<>();
        for (PicUrls url : pic_urls) {
            urls.add(new PhotoViewInfo(url.getOriginal_pic()));
        }
        if (urls.size() == 1) {
            holder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentActivity activity = (FragmentActivity) context;
                    GPreviewBuilder.from(activity)
                            .setData(urls)
                            .setCurrentIndex(0)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();
                }
            });
        } else {
            holder.gv_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    //ToastUtils.showToast(context,"index"+index, Toast.LENGTH_SHORT);
                    FragmentActivity activity = (FragmentActivity) context;
                    GPreviewBuilder.from(activity)
                            .setData(urls)
                            .setCurrentIndex(index)
                            .setType(GPreviewBuilder.IndicatorType.Dot)
                            .start();
                }
            });
        }
        return convertView;
    }
    /**
     ** 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
    /*private void computeBoundsBackward(int firstCompletelyVisiblePos,ArrayList<PicUrls> urls) {
        for (int i = firstCompletelyVisiblePos;i < mThumbViewInfoList.size(); i++) {
            View itemView = mGridLayoutManager.findViewByPosition(i);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
                thumbView.getGlobalVisibleRect(bounds);
            }
            mThumbViewnfoList.get(i).setBounds(bounds);
        }
    }*/

    /**
     * @param status
     * @param imgContainer
     * @param gv_images
     * @param iv_image
     */
    private void setImages(Status status, FrameLayout imgContainer, GridView gv_images, ImageView iv_image) {
        ArrayList<PicUrls> pic_urls = status.getPic_urls();
        String thumbnail_pic = status.getThumbnail_pic();
        //图片多余一张时
        if (pic_urls != null && pic_urls.size() > 1) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.GONE);

            StatusGridImageAdapter gvAdapter = new StatusGridImageAdapter(context, pic_urls);
            gv_images.setAdapter(gvAdapter);
        } else if (thumbnail_pic != null) {
            imgContainer.setVisibility(View.VISIBLE);
            gv_images.setVisibility(View.GONE);
            iv_image.setVisibility(View.VISIBLE);

            imageLoader.displayImage(thumbnail_pic, iv_image);
        } else {
            imgContainer.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {
        public LinearLayout ll_card_content;
        public ImageView iv_avatar;
        public RelativeLayout rl_content;
        public TextView tv_subhead;
        public TextView tv_caption;

        public TextView tv_content;
        public FrameLayout include_status_image;
        public GridView gv_images;
        public ImageView iv_image;

        public LinearLayout include_retweeted_status;
        public TextView tv_retweeted_content;
        public FrameLayout include_retweeted_status_image;
        public GridView gv_retweeted_images;
        public ImageView iv_retweeted_image;

        public LinearLayout ll_share_bottom;
        public ImageView iv_share_bottom;
        public TextView tv_share_bottom;
        public LinearLayout ll_comment_bottom;
        public ImageView iv_comment_bottom;
        public TextView tv_comment_bottom;
        public LinearLayout ll_like_bottom;
        public ImageView iv_like_bottom;
        public TextView tv_like_bottom;
    }
}
