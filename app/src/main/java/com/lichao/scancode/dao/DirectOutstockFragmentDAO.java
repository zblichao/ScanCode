package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-04-06.
 */
public class DirectOutstockFragmentDAO extends CommonDAO {
    /**
     * 根据条形码查找产品
     *
     * @param barcode
     * @return
     */
    public String searchProductByCode(String barcode, String warehouse_id) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("barcode", barcode));
        params.add(new NameValuePair("action", "out_info"));
        params.add(new NameValuePair("warehouse_id", warehouse_id));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String outOrders(String department, String product_id, String qty,   String LOT, String expire,    String warehouseId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("action", "out_dispatch"));
        params.add(new NameValuePair("department", department));
        params.add(new NameValuePair("product_id", product_id));
        params.add(new NameValuePair("qty", qty));
        params.add(new NameValuePair("LOT", LOT));
        params.add(new NameValuePair("expire", expire));
        params.add(new NameValuePair("warehouse_id", warehouseId));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
