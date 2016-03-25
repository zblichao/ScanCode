package com.lichao.scancode.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zblichao on 2016-03-02.
 */
public class CheckNetWorkUtils {
    public static boolean updateConnectedFlags(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {

            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI
                    || activeInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    || activeInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {

                return true;

            } else {

                return false;

            }

        } else {

            return false;

        }
    }
}
