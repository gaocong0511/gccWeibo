package com.nonk.gaocongdeweibo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nonk.gaocongdeweibo.BaseApplication;
import com.nonk.gaocongdeweibo.BaseFragment;
import com.nonk.gaocongdeweibo.Bean.User;
import com.nonk.gaocongdeweibo.Bean.UserItem;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.adapter.UserItemAdapter;
import com.nonk.gaocongdeweibo.gccApi.GccApi;
import com.nonk.gaocongdeweibo.utils.TitleBuilder;
import com.nonk.gaocongdeweibo.widget.WarpHeightListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class UserFragment extends BaseFragment {
	private LinearLayout ll_userinfo;

	private ImageView iv_avater;
	private TextView tv_subhead;
	private TextView tv_caption;

	private TextView tv_status_count;
	private TextView tv_follows_count;
	private TextView tv_fans_count;

	private WarpHeightListView lv_user_items;

	private User user;
	private View view;

	private UserItemAdapter adapter;
	private List<UserItem> userItems;

	private ImageLoader imageLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(activity, R.layout.frag_user, null);
		imageLoader=ImageLoader.getInstance();
		initView();

		setItem();
		return view;
	}


	private void initView() {
		//初始化标题栏
		new TitleBuilder(view).setTitleText("我").build();

		//用户信息
		ll_userinfo=(LinearLayout)view.findViewById(R.id.ll_user_info);
		ll_userinfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
		iv_avater=(ImageView)view.findViewById(R.id.iv_avater);
		tv_subhead=(TextView)view.findViewById(R.id.tv_subhead);
		tv_caption=(TextView)view.findViewById(R.id.tv_caption);
		//互动信息栏：微博数 关注数 粉丝数
		tv_status_count=(TextView)view.findViewById(R.id.tv_status_count);
		tv_follows_count=(TextView)view.findViewById(R.id.tv_follow_count);
		tv_fans_count =(TextView)view.findViewById(R.id.tv_fans_count);
		//列表栏
		lv_user_items=(WarpHeightListView)view.findViewById(R.id.lv_user_items);
		userItems=new ArrayList<>();
		adapter=new UserItemAdapter(activity,userItems);
		lv_user_items.setAdapter(adapter);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			Oauth2AccessToken accessToken= AccessTokenKeeper.readAccessToken(activity);
			String token=accessToken.getToken();
			RequestParams params=new RequestParams();
			params.add("access_token",token);
			params.add("uid",accessToken.getUid());
			GccApi.get("users/show.json", params, new TextHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					BaseApplication application=(BaseApplication)activity.getApplication();
					user=new Gson().fromJson(responseString,User.class);
					application.currentUser=user;
					setUserInfo();
				}
			});
		}
	}

	private void setUserInfo() {
		tv_subhead.setText(user.getName());
		tv_caption.setText("简介"+user.getDescription());
		imageLoader.displayImage(user.getAvatar_large(),iv_avater);
		tv_status_count.setText(user.getStatuses_count()+"");
		tv_fans_count.setText(user.getFollowers_count()+"");
		tv_follows_count.setText(user.getFriends_count()+"");
	}

	/**
	 * 设置listView中的信息
	 */
	private void setItem() {
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_1, "新的朋友", ""));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博等级", "Lv13"));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_3, "编辑资料", ""));
		userItems.add(new UserItem(true, R.drawable.push_icon_app_small_4, "我的相册", "(18)"));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_5, "我的点评", ""));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_4, "我的赞", "(32)"));
		userItems.add(new UserItem(true, R.drawable.push_icon_app_small_3, "微博支付", ""));
		userItems.add(new UserItem(false, R.drawable.push_icon_app_small_2, "微博运动", "步数、卡路里、跑步轨迹"));
		userItems.add(new UserItem(true, R.drawable.push_icon_app_small_1, "更多", "收藏、名片"));
		adapter.notifyDataSetChanged();
	}
}
