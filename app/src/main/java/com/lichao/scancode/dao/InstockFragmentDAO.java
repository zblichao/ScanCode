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
public class InstockFragmentDAO extends CommonDAO {
    /**
     * @param product_id (产品id)
     * @param dtStart（有限期）
     * @param det_rowid（订单rowid）
     * @param order_id（订单id）
     * @param pu（价格）
     * @param dispatch_qty（入库数量）
     * @param LOT（lot）
     * @return
     */
    public String  instock(String warehouse_id,String product_id, String dtStart, String det_rowid,String qualified_rowid ,String order_id, String pu, String dispatch_qty,String remain_qty, String LOT) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new NameValuePair("action", "dispatch"));
        nameValuePairs.add(new NameValuePair("id", String.valueOf(order_id)));
        nameValuePairs.add(new NameValuePair("fk_commandefourndet_0_0",  det_rowid ));
        nameValuePairs.add(new NameValuePair("fk_commandefourn_qualify_0_0",  qualified_rowid ));
        nameValuePairs.add(new NameValuePair("product_0_0",  product_id));
        nameValuePairs.add(new NameValuePair("pu_0_0", pu));
        nameValuePairs.add(new NameValuePair("lot_number_0_0",LOT));

         Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(dtStart));
            nameValuePairs.add(new NameValuePair("dlc_0_0", dtStart.replace('-', '/')));
            nameValuePairs.add(new NameValuePair("dlc_0_0day", String.valueOf(c.get(Calendar.DAY_OF_MONTH))));
            nameValuePairs.add(new NameValuePair("dlc_0_0month", String.valueOf(c.get(Calendar.MONTH) + 1)));
            nameValuePairs.add(new NameValuePair("dlc_0_0year", String.valueOf(c.get(Calendar.YEAR))));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nameValuePairs.add(new NameValuePair("qty_0_0", dispatch_qty));
        nameValuePairs.add(new NameValuePair("qty_remain_0_0", dispatch_qty));
        nameValuePairs.add(new NameValuePair("entrepot_0_0", warehouse_id));
        nameValuePairs.add(new NameValuePair("mobile", "1"));

        String res = "";
        try {
            res = HttpUtil.Post("fourn/commande/dispatch.php", nameValuePairs);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return res;

    }
}
