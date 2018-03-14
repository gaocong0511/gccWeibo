package com.nonk.gaocongdeweibo.Activity;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.nonk.gaocongdeweibo.Fragment.FragmentController;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.utils.ToastUtils;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private RadioGroup rg_tab;
    private ImageView iv_add;
    private FragmentController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        controller = FragmentController.getInstance(this, R.id.f1_content);

        controller.showFragment(0);
        initView();
    }

    private void initView() {
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        iv_add = (ImageView) findViewById(R.id.iv_add);

        rg_tab.setOnCheckedChangeListener(this);
        iv_add.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                controller.showFragment(0);
                break;
            case R.id.rb_message:
                controller.showFragment(1);
                break;
            case R.id.rb_search:
                controller.showFragment(2);
                break;
            case R.id.rb_user:
                controller.showFragment(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                ToastUtils.showToast(this, "add", Toast.LENGTH_SHORT);
                //调用写微博页面
                break;

            default:
                break;
        }
    }
}
