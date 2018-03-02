package com.nonk.gaocongdeweibo.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseFragment;
import com.nonk.gaocongdeweibo.Bean.Status;
import com.nonk.gaocongdeweibo.Bean.StatusTimeLineResponse;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.StatusAdapter;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.Logger;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.utils.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends BaseFragment {

    private View view;
    private TextView titlebar_tv;
    private ListView lv_home;
    private List<Status> statuses =new ArrayList<>();
    private int curPage=1;
    private StatusAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initData(1);
        initViews();
        RefreshLayout refreshLayout=(RefreshLayout)view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                initData(1);
                refreshLayout.finishRefresh(2000/*,false*/);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                initData(curPage+1);
                refreshLayout.finishLoadMore(2000/*,false*/);
            }
        });

        return view;
    }

    private void initViews(){
        view = View.inflate(activity, R.layout.frag_home, null);
        adapter=new StatusAdapter(activity,statuses);
        new TitleBuilder(view)
                .setTitleText("首页")
                .setLeftText("LEFT")
                .setLeftOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(activity, "left onclick", Toast.LENGTH_SHORT);
                    }
                });
        lv_home = (ListView) view.findViewById(R.id.lv_home);
        lv_home.setAdapter(adapter);
    }
    private void initData(final int page) {
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(activity);
        String token = mAccessToken.getToken();
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("access_token", token);
        GccApi.get("statuses/home_timeline.json", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("json", responseString);
                StatusTimeLineResponse timeLineResponse=new Gson().fromJson(responseString,StatusTimeLineResponse.class);
                if(page==1){
                    statuses.clear();
                }
                curPage=page;
                //lv_home.setAdapter(new StatusAdapter(activity, timeLineResponse.getStatuses()));
                addStatus(timeLineResponse);
            }
        });
    }
    private void addStatus(StatusTimeLineResponse statusBeans){
        for (Status status:statusBeans.getStatuses()){
            if(!statuses.contains(status)){
                statuses.add(status);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
