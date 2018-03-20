package com.nonk.gaocongdeweibo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.EmotionGvAdapter;
import com.nonk.gaocongdeweibo.adapter.EmotionPagerAdapter;
import com.nonk.gaocongdeweibo.adapter.WriteStatusGridImgsAdapter;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.DisplayUtils;
import com.nonk.gaocongdeweibo.utils.EmotionUtils;
import com.nonk.gaocongdeweibo.utils.ImageUtils;
import com.nonk.gaocongdeweibo.utils.StringUtils;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.utils.ToastUtils;
import com.nonk.gaocongdeweibo.widget.WarpHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

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
        et_write_staus = (EditText) findViewById(R.id.et_write_status);
        //添加的九宫格图片
        gv_write_status = (WarpHeightGridView) findViewById(R.id.gv_write_status);
        //转发微博内容
        include_retweeted_status_card = findViewById(R.id.include_retweeted_status_card);
        iv_retweeted_status_img = (ImageView) findViewById(R.id.iv_rstatus_img);
        tv_retweeted_status_username = (TextView) findViewById(R.id.tv_rstatus_username);
        tv_retweeted_status_content = (TextView) findViewById(R.id.tv_rstatus_content);
        //底部添加栏
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_at = (ImageView) findViewById(R.id.iv_at);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
        iv_topic = (ImageView) findViewById(R.id.iv_topic);

        //表情选择面板
        ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
        vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
        //进度框
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("微博发布中......请稍后");

        statusGridImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
        gv_write_status.setAdapter(statusGridImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        iv_image.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        imageLoader = ImageLoader.getInstance();
        initRetweetedStatus();
        initEmotion();
    }

    /**
     * 设置引用的微博部分
     */
    private void initRetweetedStatus() {
        if (retweeted_status != null) {
            Status repostStatus = retweeted_status.getRetweeted_status();
            if (repostStatus != null) {
                String content = "//@" + retweeted_status.getUser().getName() + ":" + retweeted_status.getText();
                et_write_staus.setText(StringUtils.getWeiboContent(this, et_write_staus, content));
                cardStatus=repostStatus;
            } else {
                et_write_staus.setText("转发微博");
                cardStatus = retweeted_status;
            }

            String imgUrl = cardStatus.getThumbnail_pic();
            if (TextUtils.isEmpty(imgUrl)) {
                iv_retweeted_status_img.setVisibility(View.GONE);
            } else {
                iv_image.setVisibility(View.VISIBLE);
                imageLoader.displayImage(imgUrl, iv_retweeted_status_img);
            }

            tv_retweeted_status_username.setText("@" + cardStatus.getUser().getName());
            tv_retweeted_status_content.setText(cardStatus.getText());

            iv_image.setVisibility(View.GONE);

            include_retweeted_status_card.setVisibility(View.VISIBLE);
        } else {
            include_retweeted_status_card.setVisibility(View.GONE);
        }
    }

    private void initEmotion() {
        int screenWidth = DisplayUtils.getScreenWidthPixels(this);
        int screenHeight = DisplayUtils.getScreenHeightPixels(this);
        int spacing = DisplayUtils.dpToPx(this, 8);
        int itemWidth = (screenWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        for (String emojiName : EmotionUtils.emojiMap.keySet()) {
            emotionNames.add(emojiName);

            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);

                emotionNames = new ArrayList<>();
            }

            if (emotionNames.size() == 0) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
            }

            emotionPagerAdapter = new EmotionPagerAdapter(gvs);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
            vp_emotion_dashboard.setLayoutParams(params);
            vp_emotion_dashboard.setAdapter(emotionPagerAdapter);
        }
    }

    /**
     * 创建显示表情的GridView
     *
     * @param emotionNames
     * @param gvWidth
     * @param padding
     * @param itemWidth
     * @param gvHeight
     * @return
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setPadding(padding, padding, padding, padding);
        gv.setNumColumns(7);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);

        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }

    /**
     * 发送微博的入口
     */
    private void sendStatus() {
        String statusContent = et_write_staus.getText().toString();
        if (TextUtils.isEmpty(statusContent)) {
            showToast("微博内容不能为空");
            return;
        }

        String imgFilePath = null;
        if (imgUris.size() > 0) {
            Uri uri = imgUris.get(0);
            imgFilePath = ImageUtils.getRealPathFromUri(this, uri);
        }
        Bitmap bm = BitmapFactory.decodeFile(imgFilePath);
        //转发微博的存储的信息
        long retweedStatusId = cardStatus == null ? -1 : cardStatus.getId();

        String postUrl = "statuses/";
        RequestParams params = new RequestParams();
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        String token = accessToken.getToken();
        params.add("access_token", token);
        params.add("status", statusContent);
        if (retweedStatusId > 0) {
            postUrl += "repost.json";
            params.add("id", retweedStatusId + "");
        } else if (!TextUtils.isEmpty(imgFilePath)) {
            postUrl += "upload.json";
            //发图片时没有进行压缩  直接进行put  然后将图片给放进去
            File myFile = new File(imgFilePath);
            try {
                params.put("pic", myFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            postUrl += "update.json";
        }
        GccApi.post(postUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.showToast(WriteStatusActivity.this, "发送失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ToastUtils.showToast(WriteStatusActivity.this, "发送成功", Toast.LENGTH_SHORT);
                WriteStatusActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                ImageUtils.showImagePickDialog(this);
                break;
            case R.id.iv_add:
                break;
            case R.id.iv_topic:
                break;
            case R.id.iv_emoji:
                if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
                    ll_emotion_dashboard.setVisibility(View.GONE);
                    iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
                } else {
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                    iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
                }
                break;
            case R.id.iv_at:
                break;
            case R.id.titlebar_tv_right:
                showToast("jaja");
                sendStatus();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();
        //如果点击事件发生在GridView的表格布局的图片上
        if (itemAdapter instanceof WriteStatusActivity) {
            if (position == statusGridImgsAdapter.getCount() - 1) {
                ImageUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            EmotionGvAdapter adapter = (EmotionGvAdapter) itemAdapter;
            //如果点击的是删除表情按钮
            if (position == emotionPagerAdapter.getCount() - 1) {
                et_write_staus.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                //如果点击的是表情的话
                //获得表情
                String emotionName = adapter.getItem(position);
                //获得当前正在编辑的位置
                int curPosition = et_write_staus.getSelectionStart();
                StringBuilder stringBuilder = new StringBuilder(et_write_staus.getText().toString());
                stringBuilder.insert(curPosition, emotionName);
                SpannableString content = StringUtils.getWeiboContent(this, et_write_staus, stringBuilder.toString());
                et_write_staus.setText(content);
                et_write_staus.setSelection(curPosition + emotionName.length());
            }

        }
    }

    /**
     * 当前的评论图片内容有更新时，刷新
     */
    private void updateImgs() {
        if (imgUris.size() > 0) {
            gv_write_status.setVisibility(View.VISIBLE);
            statusGridImgsAdapter.notifyDataSetChanged();
        } else {
            gv_write_status.setVisibility(View.GONE);
        }
    }

    /**
     * 重写当activity返回结果时的动作
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (requestCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = ImageUtils.imageUriFromCamera;

                imgUris.add(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (requestCode == RESULT_CANCELED) {
                    return;
                }
                Uri image = data.getData();
                imgUris.add(image);
                updateImgs();
                break;
        }
    }

    private String encode(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //Log.d(TAG, "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
        //mTvShow.setText(encodeString);
        return encodeString;
    }
}
