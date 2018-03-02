package com.nonk.gaocongdeweibo;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nonk.gaocongdeweibo.Activity.MainActivity;


public class BaseFragment extends Fragment {
    protected MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=(MainActivity)getActivity();
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity){
        Intent intent=new Intent(activity,tarActivity);
        startActivity(intent);
    }
}
