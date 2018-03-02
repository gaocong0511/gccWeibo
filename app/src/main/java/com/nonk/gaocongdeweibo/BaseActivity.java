package com.nonk.gaocongdeweibo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nonk.gaocongdeweibo.constants.CommonConstants;
import com.nonk.gaocongdeweibo.utils.Logger;
import com.nonk.gaocongdeweibo.utils.ToastUtils;


public class BaseActivity extends Activity {
    protected String TAG;

    protected BaseApplication application;
    protected SharedPreferences sp;
    protected Gson gson;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        application = (BaseApplication) getApplication();
        sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    protected void showToast(String msg) {
        ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
    }

    protected void showLog(String msg) {
        Logger.show(TAG, msg);
    }
}
