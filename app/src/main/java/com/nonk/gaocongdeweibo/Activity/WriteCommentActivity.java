package com.nonk.gaocongdeweibo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import cz.msebera.android.httpclient.Header;

/**
 * Created by monk on 2018/3/5.
 */

public class WriteCommentActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_write_status;
    //底部按钮
    private ImageView iv_iamge;
    private ImageView iv_at;
    private ImageView iv_topic;
    private ImageView iv_emoji;
    private ImageView iv_add;

    //待评论的微博
    private Status status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actity_write_status);

        //获取从Intent传入的微博
        status = (Status) getIntent().getSerializableExtra("status");
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        new TitleBuilder(this)
                .setTitleText("发评论")
                .setLeftText("取消")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WriteCommentActivity.this.finish();
                    }
                })
                .setRightText("发送").setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComments();
            }
        });

        et_write_status = (EditText) findViewById(R.id.et_write_status);
        iv_iamge = (ImageView) findViewById(R.id.iv_image);
        iv_at = (ImageView) findViewById(R.id.iv_at);
        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_topic = (ImageView) findViewById(R.id.iv_topic);
        iv_emoji = (ImageView) findViewById(R.id.iv_emoji);

        iv_emoji.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        iv_topic.setOnClickListener(this);
        iv_at.setOnClickListener(this);
        iv_iamge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_image:
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

    private void sendComments() {
        String comment = et_write_status.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            showToast("评论内容不可以为空");
            return;
        }

        Oauth2AccessToken accessToken= AccessTokenKeeper.readAccessToken(this);
        String token=accessToken.getToken();
        RequestParams params=new RequestParams();
        params.put("comment",comment);
        params.put("id",status.getId());
        params.put("access_token",token);
        GccApi.post("comments/create.json", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showToast("发送失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                showToast("评论发送成功");

                //评论发送成功之后，设置Result返回数据，然后关闭本页面
                Intent data=new Intent();
                data.putExtra("sendComment",true);
                setResult(RESULT_OK,data);

                WriteCommentActivity.this.finish();
            }
        });

    }
}
