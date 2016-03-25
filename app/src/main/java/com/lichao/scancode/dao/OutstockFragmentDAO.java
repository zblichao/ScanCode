package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-11.
 */
public class OutstockFragmentDAO extends CommonDAO {
    /**
     * 获取所有出库订单
     * @return
     */
    public String getOutOrders() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("action", "get_out_orders"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
