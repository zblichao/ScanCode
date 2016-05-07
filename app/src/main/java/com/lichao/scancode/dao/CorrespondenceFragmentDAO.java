package com.lichao.scancode.dao;

import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.http.HttpUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zblichao on 2016-04-15.
 */
public class CorrespondenceFragmentDAO  extends  CommonDAO{
    /**
 * @param product_barcode (原码)
 * @param hospital_barcode（院内码）
 * @return
 */
public String qualify(String product_barcode, String hospital_barcode) {

    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new NameValuePair("action", "barcode_raw"));
    nameValuePairs.add(new NameValuePair("product_barcode", product_barcode));
    nameValuePairs.add(new NameValuePair("hospital_barcode", hospital_barcode));

    String res = "";
    try {
        res = HttpUtil.Post("index.php", nameValuePairs);
    } catch (IOException e) {
        e.printStackTrace();
    }

    return res;

}
}
