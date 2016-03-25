package com.lichao.scancode.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lichao.scancode.entity.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zblichao on 2016-03-10.
 */
public class SharedPreferencesUtil {
    public static Set keySet = new HashSet();
    static {
        keySet.add("preCity");
        keySet.add("Y");
        keySet.add("X");
        keySet.add("locAddress");
        keySet.add("locCity");
    }
    public static User getUser(Context activity) {
        User user = new User();
        SharedPreferences sp = activity.getSharedPreferences("SP",
                activity.MODE_PRIVATE);
        user.setId(sp.getString("user.id", ""));
        user.setUserName(sp.getString("user.userName", ""));
        user.setName(sp.getString("user.name", ""));
        user.setPassword(sp.getString("user.password", ""));
        user.setPermission(sp.getString("user.permission", ""));


        return user;
    }

    public static void saveUser(Context activity, User user) {
        SharedPreferences sp = activity.getSharedPreferences("SP",
                activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user.id", user.getId());
        editor.putString("user.userName", user.getUserName());
        editor.putString("user.name", user.getName());
        editor.putString("user.password", user.getPassword());
        editor.putString("user.permission",user.getPermission());

        editor.commit();
    }
}
