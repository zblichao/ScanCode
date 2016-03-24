package com.lichao.scancode;

import android.app.Application;

import com.lichao.scancode.entity.User;
import com.lichao.scancode.util.SharedPreferencesUtil;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by zblichao on 2016-03-10.
 */
public class MyApplication extends Application {
    private User user;
    public static MyApplication myApplication;
    public static CookieManager cookieManager;

    @Override
    public void onCreate() {
        super.onCreate();
        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        myApplication = this;
    }

    public User getUser() {
        if (user == null) {
            this.user = SharedPreferencesUtil.getUser(this);
        }
        return user;
    }

    public void setUser(User user) {
        SharedPreferencesUtil.saveUser(this, user);
        this.user = user;
    }
}
