package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-12.
 */
public class CommonDAO {

    /**
     * 根据条形码查找产品
     *
     * @param barcode
     * @return
     */
    public String searchProductByCode(String barcode) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("barcode", barcode));
        params.add(new NameValuePair("action", "get_info"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return res;
    }

    /**
     * 显示所有可用的库房
     *
     * @return
     */
    public String getWarehouses() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("action", "show_all_warehouses"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
