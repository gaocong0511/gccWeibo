package com.nonk.gaocongdeweibo.Activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.Bean.StatusTimeLineResponse;
import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.StatusAdapter;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.ImageOptHelper;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.widget.UnderlineIndicatorView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by monk on 2018/3/22.
 */

public class UserInfoActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private View title;
    private ImageView iv_title_left;
    private TextView titlebar_tv;
    //headerView 用户信息
    private View user_info_head;
    private ImageView iv_avater;
    private TextView tv_name;
    private TextView tv_follows;
    private TextView tv_fans;
    private TextView tv_sign;

    //shadow_tab 顶部悬浮的菜单栏
    private View shadow_user_info_tab;
    private RadioGroup shadow_rg_user_info;
    private UnderlineIndicatorView shadow_ul_iv_user_info;
    //下面的
    private View user_info_tab;
    private RadioGroup rg_user_info;
    private UnderlineIndicatorView ul_iv_user_info;

    //headerView 添加至列表中作为header的菜单栏
    private ImageView iv_user_info_head;
    private RefreshLayout refreshLayout;
    private ListView lv_status;
    private View footView;
    //用户相关信息
    private boolean isCurrentUser;
    private User user;
    private String username;
    //个人微博列表
    private List<Status> statuses = new ArrayList<>();
    private StatusAdapter adapter;
    private long currentPage = 1;
    //背景图片的最小高度
    private int minImageHeight = -1;
    //背景图片的最大高度
    private int maxImageHeight = -1;

    private int curScrollY;

    private ImageLoader imageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        username = getIntent().getStringExtra("username");
        if (TextUtils.isEmpty(username)) {
            isCurrentUser = true;
            user = application.currentUser;
        }
        gson=new Gson();
        imageLoader = ImageLoader.getInstance();
        initView();
        loadData();
    }

    private void loadData() {
        if (isCurrentUser) {
            setUserInfo();
        } else {
            loadUserInfo();
        }
        loadStatus(1);
    }

    /**
     * 设置当前用户的信息
     */
    private void setUserInfo() {
        if (user == null) {
            return;
        }
        tv_name.setText(user.getName());
        titlebar_tv.setText(user.getName());
        imageLoader.displayImage(user.getAvatar_large(), new ImageViewAware(iv_avater),
                ImageOptHelper.getAvatarOptions());

        tv_follows.setText("关注" + user.getFriends_count());
        tv_fans.setText("粉丝" + user.getFollowers_count());
        tv_sign.setText("简介" + user.getDescription());
    }

    /**
     * 加载指定用户的信息
     */
    private void loadUserInfo() {
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        String token = accessToken.getToken();
        RequestParams params = new RequestParams();
        params.add("access_token", token);
        params.add("screen_name", username);
        GccApi.get("users/show.json", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                user = gson.fromJson(responseString, User.class);
                setUserInfo();
            }
        });
    }

    private void initView() {
        title = new TitleBuilder(this)
                .setTitleBgRes(R.drawable.userinfo_navigationbar_background)
                .setLeftImage(R.drawable.navigationbar_back_sel)
                .setLeftOnClickListener(this)
                .build();
        iv_title_left = title.findViewById(R.id.titlebar_iv_left);
        titlebar_tv = title.findViewById(R.id.titlebar_tv);
        initInfoHead();
        initTab();
        initLitView();
    }


    private void initInfoHead() {
        iv_user_info_head = (ImageView) findViewById(R.id.iv_user_info_head);
        user_info_head = View.inflate(this, R.layout.user_info_head, null);
        iv_avater = user_info_head.findViewById(R.id.iv_avater);
        tv_name = user_info_head.findViewById(R.id.tv_name);
        tv_follows = user_info_head.findViewById(R.id.tv_follows);
        tv_fans = user_info_head.findViewById(R.id.tv_fans);
        tv_sign = user_info_head.findViewById(R.id.tv_sign);
    }

    /**
     * 设置悬浮显示的菜单栏和底部的菜单栏
     */
    private void initTab() {
        //悬浮显示的菜单栏
        shadow_user_info_tab = findViewById(R.id.user_info_tab);
        shadow_rg_user_info = (RadioGroup) findViewById(R.id.rg_user_info);
        shadow_ul_iv_user_info = findViewById(R.id.ul_iv_user_info);

        shadow_rg_user_info.setOnCheckedChangeListener(this);
        shadow_ul_iv_user_info.setCurrentItemWidthOutAnim(1);

        //设置添加到列表中的菜单栏
        user_info_tab = View.inflate(this, R.layout.user_info_tab, null);
        rg_user_info = (RadioGroup) user_info_tab.findViewById(R.id.rg_user_info);
        ul_iv_user_info = user_info_tab.findViewById(R.id.ul_iv_user_info);

        rg_user_info.setOnCheckedChangeListener(this);
        ul_iv_user_info.setCurrentItemWidthOutAnim(1);
    }

    /**
     * 初始化当前界面中的listView
     */
    @SuppressLint("NewApi")
    private void initLitView() {
        refreshLayout = findViewById(R.id.refershLayout);
        lv_status = findViewById(R.id.lv_status);
        adapter = new StatusAdapter(this, statuses);
        lv_status.setAdapter(adapter);
        lv_status.addHeaderView(user_info_head);
        lv_status.addHeaderView(user_info_tab);

        //设置refreshlayout的刷新操作
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadStatus(1);
            }

            
        });

        //RelativeLayout layout=findViewById(R.id.rl_user_info_page);


       // NestedScrollView view=findViewById(R.id.scrollView);

        /*view.setOnScrollChangeListener(new View.OnScrollChangeListener() {

            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                curScrollY = scrollY;
                if (minImageHeight == -1) {
                    minImageHeight = iv_user_info_head.getHeight();
                }
                if (maxImageHeight == -1) {
                    Rect rect = iv_user_info_head.getDrawable().getBounds();
                    maxImageHeight = rect.bottom - rect.top;
                }

                if (minImageHeight - scrollY < maxImageHeight) {
                    iv_user_info_head.layout(0, 0, iv_user_info_head.getHeight(), minImageHeight - scrollY);
                } else {
                    iv_user_info_head.layout(0,
                            -scrollY - (maxImageHeight - minImageHeight),
                            iv_user_info_head.getWidth(),
                            -scrollY - (maxImageHeight - minImageHeight) + iv_user_info_head.getHeight());
                }
            }
        });*/

        iv_user_info_head.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View viewint, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (curScrollY == bottom - oldBottom) {
                    iv_user_info_head.layout(0, 0, iv_user_info_head.getWidth(), oldBottom);
                }
            }
        });

        lv_status.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                iv_user_info_head.layout(0,
                        user_info_head.getTop(),
                        iv_user_info_head.getWidth(),
                        user_info_head.getTop() + iv_user_info_head.getHeight());

                if (user_info_head.getBottom() < title.getBottom()) {
                    shadow_user_info_tab.setVisibility(View.VISIBLE);
                    title.setBackgroundResource(R.drawable.navigationbar_background);
                    titlebar_tv.setVisibility(View.VISIBLE);
                    iv_title_left.setImageResource(R.drawable.navigationbar_back_sel);
                } else {
                    shadow_user_info_tab.setVisibility(View.GONE);
                    title.setBackgroundResource(R.drawable.userinfo_navigationbar_background);
                    iv_title_left.setImageResource(R.drawable.userinfo_navigationbar_back_sel);
                    titlebar_tv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadStatus(final long page) {
        RequestParams params = new RequestParams();
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        String token = accessToken.getToken();
        params.add("access_token", token);
        if (user == null) {
            params.add("screen_name", username);
        } else {
            params.add("uid", user.getId() + "");

        }
        params.add("page", page + "");
        GccApi.get("statuses/user_timeline.json", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (page == 1) {
                    statuses.clear();
                }
                addStatus(gson.fromJson(responseString, StatusTimeLineResponse.class));
                refreshLayout.finishRefresh();
            }
        });
    }

    private void addStatus(StatusTimeLineResponse statusTimeLineResponse) {
        for (Status status : statusTimeLineResponse.getStatuses()) {
            if (!statuses.contains(status)) {
                statuses.add(status);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_iv_left:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        int index = radioGroup.indexOfChild(radioGroup.findViewById(checkedId));

        if (shadow_user_info_tab.getVisibility() == View.VISIBLE) {
            shadow_ul_iv_user_info.setCurrentItem(index);

            ((RadioButton) rg_user_info.findViewById(checkedId)).setChecked(true);
            ul_iv_user_info.setCurrentItemWidthOutAnim(index);
        } else {
            ul_iv_user_info.setCurrentItem(index);

            ((RadioButton) shadow_rg_user_info.findViewById(checkedId)).setChecked(true);
            shadow_ul_iv_user_info.setCurrentItemWidthOutAnim(index);
        }
    }
}
