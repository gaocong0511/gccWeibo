package com.nonk.gaocongdeweibo.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.R;
import com.nonk.gaocongdeweibo.constants.Constants;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

/**
 * Created by monk on 2018/3/17.
 */

public class WeicoLoginActivity extends BaseActivity {
    private String redirectUri;
    private WebView webView;

    //@SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weico_login);
        String authurl = "https://open.weibo.cn/oauth2/authorize" + "?" + "client_id=" + Constants.WEICO_APP_KEY
                + "&response_type=token&redirect_uri=" + Constants.WEICO_REDIRECT_URL
                + "&key_hash=" + Constants.WEICO_AppSecret + (TextUtils.isEmpty(Constants.WEICO_PackageName) ? "" : "&packagename=" + Constants.WEICO_PackageName)
                + "&display=mobile" + "&scope=" + Constants.WEICO_SCOPE;
        redirectUri = Constants.WEICO_REDIRECT_URL;
        webView = (WebView) findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WeicoLoginWebViewClient());
        webView.loadUrl(authurl);

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //返回操作
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private class WeicoLoginWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isUrlRedirected(url)) {
                view.stopLoading();
                handleRedirectedUrl(url);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!url.equals("about:blank") && isUrlRedirected(url)) {
                view.stopLoading();
                handleRedirectedUrl(url);
                return;
            }
            super.onPageStarted(view, url, favicon);
        }
    }

    public boolean isUrlRedirected(String url) {
        return url.startsWith(redirectUri);
    }

    private void handleRedirectedUrl(String url) {
        if (!url.contains("error")) {
            int tokenIndex = url.indexOf("access_token=");
            int expiresIndex = url.indexOf("expires_in=");
            int refresh_token_Index = url.indexOf("refresh_token=");
            int uid_Index = url.indexOf("uid=");
            int scope_Index = url.indexOf("scope=");

            String token = url.substring(tokenIndex + 13, url.indexOf("&", tokenIndex));
            String expiresIn = url.substring(expiresIndex + 11, url.indexOf("&", expiresIndex));
            String refresh_token = url.substring(refresh_token_Index + 14, url.indexOf("&", refresh_token_Index));
            String uid = new String();
            if (url.contains("scope=")) {
                uid = url.substring(uid_Index + 4, url.indexOf("&", uid_Index));
            } else {
                uid = url.substring(uid_Index + 4);
            }


//            LogUtil.d("url = " + url);
//            LogUtil.d("token = " + token);
//            LogUtil.d("expires_in = " + expiresIn);
//            LogUtil.d("refresh_token = " + refresh_token);
//            LogUtil.d("uid = " + uid);

            Oauth2AccessToken mAccessToken = new Oauth2AccessToken();
            mAccessToken.setToken(token);
            mAccessToken.setExpiresIn(expiresIn);
            mAccessToken.setRefreshToken(refresh_token);
            mAccessToken.setUid(uid);
            AccessTokenKeeper.writeAccessToken(WeicoLoginActivity.this, mAccessToken);
            Intent intent = new Intent(WeicoLoginActivity.this, MainActivity.class);
            intent.putExtra("fisrtstart", true);
            startActivity(intent);
            finish();

        }
    }
}
