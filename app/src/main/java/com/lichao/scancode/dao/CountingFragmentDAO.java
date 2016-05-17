package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zblichao on 2016-03-11.
 */
public class CountingFragmentDAO extends CommonDAO {
    public String instock(String barcode, String LOT, String expire, String warehouse_id, String qty) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new NameValuePair("action", "stock_record"));
        nameValuePairs.add(new NameValuePair("hospital_barcode", barcode));
        nameValuePairs.add(new NameValuePair("LOT",  LOT ));
        nameValuePairs.add(new NameValuePair("expire",  expire));
        nameValuePairs.add(new NameValuePair("warehouse_id",  warehouse_id));
        nameValuePairs.add(new NameValuePair("qty", qty));

        String res = "";
        try {
            res = HttpUtil.Post("index.php", nameValuePairs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;

    }
}
