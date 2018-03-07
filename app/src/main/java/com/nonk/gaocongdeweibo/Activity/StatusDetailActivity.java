package com.nonk.gaocongdeweibo.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.Comment;
import com.nonk.gaocongdeweibo.Bean.CommentsResponse;
import com.nonk.gaocongdeweibo.Bean.PicUrls;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.StatusCommentAdapter;
import com.nonk.gaocongdeweibo.adapter.StatusGridImageAdapter;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.DateUtils;
import com.nonk.gaocongdeweibo.utils.ImageOptHelper;
import com.nonk.gaocongdeweibo.utils.StringUtils;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.utils.ToastUtils;
import com.nonk.gaocongdeweibo.widget.WarpHeightGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;


import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by monk on 2018/2/27.
 */

public class StatusDetailActivity extends BaseActivity implements View.OnClickListener {
    //跳转到发评论页的code
    private static final int REQUEST_CODE_WRITE_COMMENT = 2333;

    //listView headerView - 微博信息
    private View status_detail_info;
    private ImageView iv_avater;
    private TextView tv_subhead;
    private TextView tv_caption;
    private FrameLayout include_status_iamge;
    private WarpHeightGridView gv_images;
    private ImageView iv_iamge;
    private TextView tv_content;

    private View include_retweeted_status;
    private TextView tv_retweeted_content;
    private FrameLayout fl_retweeted_imageView;
    private GridView gv_retweeted_iamges;
    private ImageView iv_retweed_iamge;

    //shadowTab 顶部悬浮的菜单栏
    private View shadow_status_detail_tab;
    private RadioGroup shadow_rg_status_detail;
    private RadioButton shadow_rb_repost;
    private RadioButton shadow_rb_comments;
    private RadioButton shadow_rb_like;

    //添加到列表之中作为header的菜单栏
    private View status_detail_tab;
    private RadioGroup rg_status_detail;
    private RadioButton rb_repost;
    private RadioButton rb_comments;
    private RadioButton rb_like;
    //下拉刷新控件
    private RefreshLayout refreshLayout;
    private ListView list_commment;
    //底部的互动栏  包括转发  评论  点赞
    private LinearLayout ll_bottom_control;
    private LinearLayout ll_share_bottom;
    private TextView tv_share_bottom;
    private LinearLayout ll_comment_bottom;
    private TextView tv_comment_bottom;
    private LinearLayout ll_like_bottom;
    private TextView tv_like_bottom;

    //详情页的微博信息
    private Status status;
    //是否需要滑动到评论部分
    private boolean scroll2Comment;
    //评论当前已经加载到的页数
    private long curPage = 1;
    //ImageLoader对象
    ImageLoader imageLoader;

    //评论合集
    private List<Comment> comments;
    private StatusCommentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //首先获取intent传入的对象
        setContentView(R.layout.activity_status_detail);

        status = (Status) getIntent().getSerializableExtra("status");
        scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);
        initView();
    }

    /**
     * 初始化界面上的元素
     */
    private void initView() {
        //初始化imageLoader
        comments=new ArrayList<>();
        imageLoader = ImageLoader.getInstance();
        initTitle();
        initDetailHead();
        initTab();
        initListView();
        initControlBar();
        setData();
        loadComments(1);
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        //leftImage需要更换--
        new TitleBuilder(this)
                .setTitleText("微博正文")
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(this);
    }

    /**
     * 初始化微博信息
     */
    private void initDetailHead() {
        status_detail_info = View.inflate(this, R.layout.item_status, null);
        status_detail_info.setBackgroundResource(R.color.white);
        status_detail_info.findViewById(R.id.ll_bottom_control).setVisibility(View.GONE);
        iv_avater = (ImageView) status_detail_info.findViewById(R.id.iv_avater);
        tv_subhead = (TextView) status_detail_info.findViewById(R.id.tv_subhead);
        tv_caption = (TextView) status_detail_info.findViewById(R.id.tv_caption);
        include_status_iamge = (FrameLayout) status_detail_info.findViewById(R.id.include_status_image);
        gv_images = (WarpHeightGridView) status_detail_info.findViewById(R.id.gv_images);
        iv_iamge = (ImageView) status_detail_info.findViewById(R.id.iv_image);
        tv_content = (TextView) status_detail_info.findViewById(R.id.tv_content);
        include_retweeted_status = status_detail_info.findViewById(R.id.include_retweeted_status);
        tv_retweeted_content = (TextView) status_detail_info.findViewById(R.id.tv_retweeted_content);
        fl_retweeted_imageView = (FrameLayout) include_retweeted_status.findViewById(R.id.include_status_image);
        gv_retweeted_iamges = (GridView) fl_retweeted_imageView.findViewById(R.id.gv_images);
        iv_retweed_iamge = (ImageView) fl_retweeted_imageView.findViewById(R.id.iv_image);
        iv_iamge.setOnClickListener(this);
    }

    /**
     * 初始化啥？
     */
    private void initTab() {
        //shadow
        shadow_status_detail_tab = findViewById(R.id.status_detail_tab);
        shadow_rg_status_detail = (RadioGroup) shadow_status_detail_tab.findViewById(R.id.rg_status_detail);
        shadow_rb_repost = (RadioButton) shadow_status_detail_tab.findViewById(R.id.detail_repost);
        shadow_rb_comments = (RadioButton) shadow_status_detail_tab.findViewById(R.id.detail_comment);
        shadow_rb_like = (RadioButton) shadow_status_detail_tab.findViewById(R.id.detail_like);
        shadow_rg_status_detail.setOnClickListener(this);
        //header
        status_detail_tab = View.inflate(this, R.layout.status_detail_tab, null);
        rg_status_detail = (RadioGroup) status_detail_tab.findViewById(R.id.rg_status_detail);
        rb_repost = (RadioButton) status_detail_tab.findViewById(R.id.detail_repost);
        rb_comments = (RadioButton) status_detail_tab.findViewById(R.id.detail_comment);
        rb_like = (RadioButton) status_detail_tab.findViewById(R.id.detail_like);
        rg_status_detail.setOnClickListener(this);
    }

    /**
     * 初始化评论列表
     */
    private void initListView() {
        //下拉空间
        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        adapter = new StatusCommentAdapter(this, comments);
        list_commment = (ListView) findViewById(R.id.list_comment);
        list_commment.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadComments(1);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                loadComments(curPage + 1);
            }
        });
        list_commment.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //shadow_status_detail_tab.setVisibility(firstVisibleItem >= 2 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initControlBar() {
        ll_bottom_control = (LinearLayout) findViewById(R.id.status_detail_controlbar);
        ll_share_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_share_bottom);
        tv_share_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_share_bottom);
        ll_comment_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_comment_bottom);
        tv_comment_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_comment_bottom);
        ll_like_bottom = (LinearLayout) ll_bottom_control.findViewById(R.id.ll_like_bottom);
        tv_like_bottom = (TextView) ll_bottom_control.findViewById(R.id.tv_like_bottom);

        ll_bottom_control.setBackgroundResource(R.color.white);
        ll_share_bottom.setOnClickListener(this);
        ll_comment_bottom.setOnClickListener(this);
        ll_like_bottom.setOnClickListener(this);
    }

    private void setData() {
        //listView headerView -微博信息
        User user = status.getUser();
        imageLoader.displayImage(user.getProfile_image_url(), iv_avater, ImageOptHelper.getAvatarOptions());
        tv_subhead.setText(user.getName());
        tv_caption.setText(String.format("%s来自%s", DateUtils.getShortTime(status.getCreated_at()), Html.fromHtml(status.getSource())));

        setImages(status,include_status_iamge,gv_images,iv_iamge);
        if (TextUtils.isEmpty(status.getText())) {
            tv_content.setVisibility(View.GONE);
        } else {
            tv_content.setVisibility(View.VISIBLE);
            SpannableString weiboContent = StringUtils.getWeiboContent(this, tv_content, status.getText());
            tv_content.setText(weiboContent);
        }

        Status retweetedStatus = status.getRetweeted_status();
        if (retweetedStatus != null) {
            include_retweeted_status.setVisibility(View.VISIBLE);
            String retweetedContent = "@" + retweetedStatus.getUser().getName() + ":" + retweetedStatus.getText();
            SpannableString weiboContent = StringUtils.getWeiboContent(this, tv_retweeted_content, retweetedContent);
            tv_retweeted_content.setText(weiboContent);
            setImages(retweetedStatus,fl_retweeted_imageView,gv_retweeted_iamges,iv_retweed_iamge);
        } else {
            include_retweeted_status.setVisibility(View.GONE);
        }

        //shadow_tab  -顶部悬浮的菜单栏
        shadow_rb_repost.setText("转发" + status.getReposts_count());
        shadow_rb_comments.setText("评论" + status.getComments_count());
        shadow_rb_like.setText("赞" + status.getAttitudes_count());

        //listView headerView -添加至列表中作为header的菜单栏
        rb_repost.setText("转发" + status.getReposts_count());
        rb_comments.setText("评论" + status.getComments_count());
        rb_like.setText("赞" + status.getAttitudes_count());

        //bottom_control
        tv_share_bottom.setText(status.getReposts_count() == 0 ?
                "转发" : status.getReposts_count() + "");
        tv_comment_bottom.setText(status.getComments_count() == 0 ?
                "评论" : status.getComments_count() + "");
        tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
                "赞" : status.getAttitudes_count() + "");

    }

    private void setImages(final Status status, ViewGroup vgContainer, GridView gvImages, final ImageView ivImage) {
        if (status == null) {
            return;
        }

        ArrayList<PicUrls> picUrls = status.getPic_urls();
        String picUrl = status.getBmiddle_pic();

        if (picUrls != null && picUrls.size() == 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gvImages.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);

            imageLoader.displayImage(picUrl, ivImage);
        } else if (picUrls != null && picUrls.size() > 1) {
            vgContainer.setVisibility(View.VISIBLE);
            gvImages.setVisibility(View.VISIBLE);
            ivImage.setVisibility(View.GONE);

            StatusGridImageAdapter imageAdapter = new StatusGridImageAdapter(this, picUrls);
            gvImages.setAdapter(imageAdapter);
        } else {
            vgContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 根据当前显示的微博的id来返回这条微博的评论列表
     *
     * @param requestPage 页数
     */
    private void loadComments(final long requestPage) {
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(this);
        String token = mAccessToken.getToken();
        long id = status.getId();
        RequestParams params = new RequestParams();
        params.put("page", requestPage);
        params.put("access_token", token);
        params.put("id", id);
        GccApi.get("comments/show.json", params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (curPage == 1) {
                    comments.clear();
                }
                curPage = requestPage;
                CommentsResponse response = new Gson().fromJson(responseString, CommentsResponse.class);
                //更新评论信息数
                tv_comment_bottom.setText("评论" + response.getTotal_number());
                addData(response);
            }
        });
    }

    /**
     * 将获取到的评论信息加入到链表之中
     *
     * @param commentsResponse
     *        评论信息
     */
    private void addData(CommentsResponse commentsResponse) {
        for (Comment comment : commentsResponse.getComments()) {
            if (!comments.contains(comment)) {
                comments.add(comment);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment_bottom:
                //打开评论页
                break;
            case R.id.ll_like_bottom:
                ToastUtils.showToast(StatusDetailActivity.this, "点赞api没有对外开放", Toast.LENGTH_SHORT);
                break;
            case R.id.ll_share_bottom:
                ToastUtils.showToast(StatusDetailActivity.this, "转发", Toast.LENGTH_SHORT);
                break;
        }
    }
}
