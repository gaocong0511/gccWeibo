package com.nonk.gaocongdeweibo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.EmotionGvAdapter;
import com.nonk.gaocongdeweibo.adapter.EmotionPagerAdapter;
import com.nonk.gaocongdeweibo.adapter.WriteStatusGridImgsAdapter;
import com.nonk.gaocongdeweibo.utils.DisplayUtils;
import com.nonk.gaocongdeweibo.utils.EmotionUtils;
import com.nonk.gaocongdeweibo.utils.ImageUtils;
import com.nonk.gaocongdeweibo.utils.StringUtils;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.widget.WarpHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by monk on 2018/3/14.
 */

public class WriteStatusActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    //输入框
    private EditText et_write_staus;
    //添加的九宫格图片
    private WarpHeightGridView gv_write_status;
    //转发微博内容
    private View include_retweeted_status_card;
    private ImageView iv_retweeted_status_img;
    private TextView tv_retweeted_status_username;
    private TextView tv_retweeted_status_content;
    //底部添加栏
    private ImageView iv_image;
    private ImageView iv_at;
    private ImageView iv_topic;
    private ImageView iv_emoji;
    private ImageView iv_add;
    //表情选择面板
    private LinearLayout ll_emotion_dashboard;
    private ViewPager vp_emotion_dashboard;
    //进度框
    private ProgressDialog progressDialog;

    private WriteStatusGridImgsAdapter statusGridImgsAdapter;
    private ArrayList<Uri> imgUris = new ArrayList<>();
    private EmotionPagerAdapter emotionPagerAdapter;

    private Status retweeted_status;
    private Status cardStatus;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);

        retweeted_status = (Status) getIntent().getSerializableExtra("status");
        initView();
    }


    private void initView() {
        new TitleBuilder(this)
                .setTitleText("发微博")
                .setLeftText("取消")
                .setRightText("发送")
                .setLeftOnClickListener(this)
                .setRightOnClickListener(this)
                .build();

        //输入框
        et_write_staus=(EditText)findViewById(R.id.et_write_status);
        //添加的九宫格图片
        gv_write_status=(WarpHeightGridView)findViewById(R.id.gv_write_status);
        //转发微博内容
        include_retweeted_status_card =findViewById(R.id.include_retweeted_status_card);
        iv_retweeted_status_img=(ImageView)findViewById(R.id.iv_rstatus_img);
        tv_retweeted_status_username=(TextView)findViewById(R.id.tv_rstatus_username);
        tv_retweeted_status_content=(TextView)findViewById(R.id.tv_rstatus_content);
        //底部添加栏
        iv_image=(ImageView)findViewById(R.id.iv_image);
        iv_add=(ImageView)findViewById(R.id.iv_add);
        iv_at=(ImageView)findViewById(R.id.iv_at);
        iv_emoji=(ImageView)findViewById(R.id.iv_emoji);
        iv_topic=(ImageView)findViewById(R.id.iv_topic);

        //表情选择面板
        ll_emotion_dashboard=(LinearLayout)findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard=(ViewPager)findViewById(R.id.vp_emotion_dashboard);
        //进度框
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("微博发布中......请稍后");

        statusGridImgsAdapter=new WriteStatusGridImgsAdapter(this,imgUris,gv_write_status);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        imageLoader=ImageLoader.getInstance();
        initRetweetedStatus();
        initEmotion();
    }

    /**
     * 设置引用的微博部分
     */
    private void initRetweetedStatus(){
        if(retweeted_status!=null){
            Status repostStatus=retweeted_status.getRetweeted_status();
            if(repostStatus!=null){
                String content="//@"+retweeted_status.getUser().getName()+":"+retweeted_status.getText();
                et_write_staus.setText(StringUtils.getWeiboContent(this,et_write_staus,content));
            }else {
                et_write_staus.setText("转发微博");
                cardStatus=retweeted_status;
            }

            String imgUrl=cardStatus.getThumbnail_pic();
            if(TextUtils.isEmpty(imgUrl)){
                iv_retweeted_status_img.setVisibility(View.GONE);
            }else {
                iv_image.setVisibility(View.VISIBLE);
                imageLoader.displayImage(imgUrl,iv_retweeted_status_img);
            }

            tv_retweeted_status_username.setText("@"+cardStatus.getUser().getName());
            tv_retweeted_status_content.setText(cardStatus.getText());

            iv_image.setVisibility(View.GONE);

            include_retweeted_status_card.setVisibility(View.VISIBLE);
        }else {
            include_retweeted_status_card.setVisibility(View.GONE);
        }
    }

    private void initEmotion(){
        int screenWidth= DisplayUtils.getScreenWidthPixels(this);
        int screenHeight=DisplayUtils.getScreenHeightPixels(this);
        int spacing=DisplayUtils.dpToPx(this,8);
        int itemWidth=(screenWidth-spacing*8)/7;
        int gvHeight=itemWidth*3+spacing*4;

        List<GridView> gvs=new ArrayList<>();
        List<String> emotionNames=new ArrayList<>();
        for(String emojiName: EmotionUtils.emojiMap.keySet()){
            emotionNames.add(emojiName);

            if(emotionNames.size()==20){
                GridView gv=createEmotionGridView(emotionNames,screenWidth,spacing,itemWidth,gvHeight);
                gvs.add(gv);

                emotionNames=new ArrayList<>();
            }

            if(emotionNames.size()==0){
                GridView gv=createEmotionGridView(emotionNames,screenWidth,spacing,itemWidth,gvHeight);
                gvs.add(gv);
            }

            emotionPagerAdapter =new EmotionPagerAdapter(gvs);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(screenWidth,gvHeight);
            vp_emotion_dashboard.setLayoutParams(params);
            vp_emotion_dashboard.setAdapter(emotionPagerAdapter);
        }
    }

    /**
     * 创建显示表情的GridView
     * @param emotionNames
     * @param gvWidth
     * @param padding
     * @param itemWidth
     * @param gvHeight
     * @return
     */
    private GridView createEmotionGridView(List<String> emotionNames,int gvWidth,int padding,int itemWidth,int gvHeight){
        GridView gv=new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setPadding(padding,padding,padding,padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(gvWidth,gvHeight);
        gv.setLayoutParams(params);

        EmotionGvAdapter adapter=new EmotionGvAdapter(this,emotionNames,itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_image:
                ImageUtils.showImagePickDialog(this);
                break;
            case R.id.iv_add:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                break;
            case R.id.iv_at:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 当前的评论图片内容有更新时，刷新
     */
    private void updateImgs(){
        if(imgUris.size()>0){
            gv_write_status.setVisibility(View.VISIBLE);
            statusGridImgsAdapter.notifyDataSetChanged();
        }else {
            gv_write_status.setVisibility(View.GONE);
        }
    }

    /**
     * 重写当activity返回结果时的动作
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if(requestCode==RESULT_CANCELED){
                    return;
                }
                Uri imageUri=ImageUtils.imageUriFromCamera;

                imgUris.add(imageUri);
                updateImgs();
                break;
        }
    }
}
