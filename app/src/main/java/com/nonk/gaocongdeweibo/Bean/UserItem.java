package com.nonk.gaocongdeweibo.Bean;

/**
 * Created by monk on 2018/3/21.
 */

public class UserItem {
    private boolean isShowTopDivider;
    private int leftImg;
    private String subhead;
    private String captoion;

    public UserItem(boolean isShowTopDivider,int leftImg,String subhead,String captoion){
        this.isShowTopDivider=isShowTopDivider;
        this.leftImg=leftImg;
        this.captoion=captoion;
        this.subhead=subhead;
    }

    public boolean isShowTopDivider() {
        return isShowTopDivider;
    }

    public void setShowTopDivider(boolean showTopDivider) {
        isShowTopDivider = showTopDivider;
    }

    public int getLeftImg() {
        return leftImg;
    }

    public void setLeftImg(int leftImg) {
        this.leftImg = leftImg;
    }

    public String getSubhead() {
        return subhead;
    }

    public void setSubhead(String subhead) {
        this.subhead = subhead;
    }

    public String getCaptoion() {
        return captoion;
    }

    public void setCaptoion(String captoion) {
        this.captoion = captoion;
    }
}
