package com.nonk.gaocongdeweibo.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

public class FragmentController {
    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;

    private static FragmentController controller;

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (controller == null) {
            controller = new FragmentController(activity,containerId);
        }
        return controller;
    }

    private FragmentController(FragmentActivity activity, int containerId) {
        this.containerId=containerId;
        fm=activity.getSupportFragmentManager();
        initFragment();
    }

    /**
     * 初始化fragment
     */
    private void initFragment(){
        fragments=new ArrayList<Fragment>();
        //填充变量
        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new SearchFragment());
        fragments.add(new UserFragment());

        FragmentTransaction ft=fm.beginTransaction();
        for(Fragment fragment:fragments){
            ft.add(containerId,fragment);
        }
        ft.commit();
    }

    /**
     * 显示某个fragment
     * @param postion 要显示的fragment的位置
     */
    public void showFragment(int postion){
        hideFragments();
        Fragment fragment=fragments.get(postion);
        FragmentTransaction ft= fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments(){
        FragmentTransaction ft=fm.beginTransaction();
        for (Fragment fragment :fragments){
            if(fragment!=null){
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    public Fragment getFragment(int postion){
        return fragments.get(postion);
    }
}
