package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-19.
 */
public class OutOrderDetialDAO {

    public String getOutOrders(String id) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("order_id", id));
        params.add(new NameValuePair("action", "get_info_out"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public String getProductInfo(String barcode,String lot,String expire) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("barcode", barcode ));
        params.add(new NameValuePair("action", "get_product_info"));
        params.add(new NameValuePair("lot", lot));
        params.add(new NameValuePair("expire",expire));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    public String outOrders(String orderId, String customerId, String qty, String productId, String lot, String originStock, String detRowId, String expire, String warehouseId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("action", "deliver"));
        params.add(new NameValuePair("order_id", orderId));
        params.add(new NameValuePair("customer_id", customerId));
        params.add(new NameValuePair("qty_0_0", qty));
        params.add(new NameValuePair("product_id_0_0", productId));
        params.add(new NameValuePair("LOT_0_0", lot));
        params.add(new NameValuePair("origin_stock_0_0", originStock));
        params.add(new NameValuePair("det_rowid_0_0", detRowId));
        params.add(new NameValuePair("expire_0_0", expire));
        params.add(new NameValuePair("warehouse_id_0_0", warehouseId));
        params.add(new NameValuePair("mobile", "1"));
        String res = "";
        try {
            res = HttpUtil.Post("index.php", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


}
