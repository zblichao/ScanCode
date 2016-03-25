package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-15.
 */
public class OrderDetailDAO {

    public String getOrderDetail(String id) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("order_id", id));
        params.add(new NameValuePair("action", "get_order_details"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
