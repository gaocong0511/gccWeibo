package com.nonk.gaocongdeweibo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nonk.gaocongdeweibo.BaseFragment;
import com.nonk.gaocongdeweibo.R;

public class UserFragment extends BaseFragment {
	
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(activity, R.layout.frag_user, null);
		return view;
	}
}
