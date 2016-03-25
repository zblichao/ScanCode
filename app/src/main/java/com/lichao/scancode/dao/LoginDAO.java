package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-10.
 */
public class LoginDAO {
    /**
     * 登陆
     *
     * @return
     */
    public String login(String username, String password) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("username", username));
        params.add(new NameValuePair("password", password));
        params.add(new NameValuePair("action", "mobile_login"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
