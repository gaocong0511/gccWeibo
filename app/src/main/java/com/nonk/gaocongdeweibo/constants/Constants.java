package com.nonk.gaocongdeweibo.constants;

public interface Constants {
    /** 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY */
    String APP_KEY      = "3768470437";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    String REDIRECT_URL = "https://www.sina.com";

    /**
     * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
     * 详情请查看 Demo 中对应的注释。
     */
    String SCOPE =
            "";

    String WEICO_APP_KEY = "211160679";
    String WEICO_REDIRECT_URL = "http://oauth.weico.cc";
    String WEICO_SCOPE = "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
    String WEICO_AppSecret = "1e6e33db08f9192306c4afa0a61ad56c";
    String WEICO_PackageName = "com.eico.weico";
}