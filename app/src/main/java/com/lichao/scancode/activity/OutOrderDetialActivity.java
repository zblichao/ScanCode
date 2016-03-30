package com.lichao.scancode.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.adapter.OutOrderDetialAdapter;
import com.lichao.scancode.dao.OutOrderDetialDAO;
import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.receiver.EAN128Parser;
import com.lichao.scancode.receiver.HIBCParser;
import com.lichao.scancode.receiver.ScanBroadcastReceiver;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.JSONHelper;
import com.lichao.scancode.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OutOrderDetialActivity extends BaseActivity {
    private ExpandableListView list;
    private OutOrderDetialAdapter adapter;
    private OutOrderDetialDAO dao;
    private String warehouseId;
    private String id;
    private String res;
    private String resProduct;
    private String resOut;
    private String barcodeStr;
    private String lot;
    private String expire;
    private View windowOut;
    private EAN128Parser ean128Parser = new EAN128Parser(); // 你看看放哪儿合适，我一般放在onCreate
    private HIBCParser hibcParser = new HIBCParser();
    private AlertDialog alertDialog;
    private Button chooseWarehouse;
    private String stock;
    private Spinner warehouse;
    private JSONArray stockArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_order_detial);
        Intent intent = getIntent();
        id = intent.getStringExtra("order_id");
        LayoutInflater inflater = LayoutInflater.from(this);
        windowOut = inflater.inflate(
                R.layout.window_out, null);
        warehouse = (Spinner) windowOut.findViewById(R.id.warehouse);
        warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject tempjson = stockArray.getJSONObject(position);
                    setTextById(tempjson, R.id.LOT, "LOT");
                    setTextById(tempjson, R.id.expire, "expire");
                    setTextById(tempjson, R.id.textqty, "qty");

                    warehouseId = tempjson.getString("warehouse_id");
                } catch (Exception e) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        list = (ExpandableListView) findViewById(R.id.list);
        adapter = new OutOrderDetialAdapter(this);
        list.setAdapter(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.SCAN_ACTION);
        registerReceiver(scanBroadcastReceiver, filter);
        scanBroadcastReceiver.initScanManger();
        dao = new OutOrderDetialDAO();
        getOrders();
        showDialog_Layout(this);
//        barcodeStr = "SPH00002159";
//        lot = "35R0902";
//        expire = "2018/09/02";
//        getProductInfo();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        List<JSONObject> temp = JSONHelper.JSONArray(res);
                        adapter.setList(temp);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(resProduct);
                        setTextById(json, R.id.productName, "product_name");
                        setTextById(json, R.id.productSize, "product_size");
                        stockArray = json.getJSONArray("stock");
                        if (stockArray != null && stockArray.length() > 0) {

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(OutOrderDetialActivity.this, R.layout.list_item, R.id.text);
                            ;
                            for (int i = 0; i < stockArray.length(); i++) {
                                adapter.add(stockArray.getJSONObject(i).getString("warehouse_name"));
                            }
                            warehouse.setAdapter(adapter);

                            JSONObject tempjson = stockArray.getJSONObject(0);
                            setTextById(tempjson, R.id.LOT, "LOT");
                            setTextById(tempjson, R.id.expire, "expire");
                            setTextById(tempjson, R.id.textqty, "qty");

                            warehouseId = tempjson.getString("warehouse_id");

                        }
                        barcodeStr = "";
                        lot = "";
                        expire = "";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (alertDialog != null && alertDialog.isShowing()) {

                        alertDialog.dismiss();
                        setTextById(R.id.productName, "");
                        setTextById(R.id.productSize, "");
                        setTextById(R.id.LOT, "");
                        setTextById(R.id.date, "");
                        setTextById(R.id.num, "");
                    }
                    ToastUtil.showLongToast(getApplicationContext(), resOut);
                    break;

            }
        }
    };

    private TextView setTextById(JSONObject json, int id, String key) {
        TextView textView = (TextView) windowOut.findViewById(id);
        try {
            textView.setText(json.getString(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textView;
    }

    private TextView setTextById(int id, String value) {
        TextView textView = (TextView) windowOut.findViewById(id);
        textView.setText(value);
        return textView;
    }

    private void getOrders() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return;
        }
        progressDialog = ProgressDialog.show(OutOrderDetialActivity.this, // context
                "", // title
                "Loading. Please wait...", // message
                true);

        new Thread() {
            @Override
            public void run() {
                super.run();
                res = dao.getOutOrders(id);
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }


    private void outOrders() {

        TextView lotText = (TextView) windowOut.findViewById(R.id.LOT);
        final String lot = lotText.getText().toString();
        TextView expireText = (TextView) windowOut.findViewById(R.id.date);
        final String expire = expireText.getText().toString();
        TextView qtyText = (TextView) windowOut.findViewById(R.id.num);
        final String qty = qtyText.getText().toString();
        progressDialog = ProgressDialog.show(OutOrderDetialActivity.this, // context
                "", // title
                "Loading. Please wait...", // message
                true);

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    List<JSONObject> temp = JSONHelper.JSONArray(res);
                    JSONObject jsonObject = new JSONObject(resProduct);
                    String orderId = id;
                    String customerId = resProduct;
                    String productId = jsonObject.getString("product_id");
                    String originStock = jsonObject.getString("");
                    String detRowId = jsonObject.getString("");
                    for (JSONObject j : temp) {
                        if (productId != null && productId.equals(j.getString("product_id"))) {
                            originStock = j.getString("origin_stock");
                            detRowId = j.getString("det_rowid");
                            customerId = j.getString("customer_id");
                        }
                    }

                    resOut = dao.outOrders(orderId, customerId, qty, productId, lot, originStock, detRowId, expire, warehouseId);
                } catch (Exception e) {

                }
                Message msg = handler.obtainMessage();
                msg.arg1 = 3;
                msg.sendToTarget();
            }
        }.start();

    }


    private void getProductInfo() {
        if (barcodeStr != null && !barcodeStr.equals("") && lot != null && !lot.equals("") && expire != null && !expire.equals("")) {
            if (progressDialog != null && progressDialog.isShowing())
                return;
            progressDialog.show();

            EditText num = (EditText) alertDialog.findViewById(R.id.num);
            num.requestFocus();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    resProduct = dao.getProductInfo(barcodeStr, lot, expire);
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 2;
                    msg.sendToTarget();
                }
            }.start();
        }
    }

    private ScanBroadcastReceiver scanBroadcastReceiver = new ScanBroadcastReceiver() {
        @Override
        public void onReceiveBarcode(String type, String barcodeStr) {
            ArrayList<NameValuePair> list;
            EditText editText;
            switch (type) {
                case "code128-P":
                    OutOrderDetialActivity.this.barcodeStr = barcodeStr;
                    OutOrderDetialActivity.this.lot = "";
                    OutOrderDetialActivity.this.expire = "";
                    break;
                case "code128-S":
//                    OutOrderDetialActivity.this.barcodeStr = barcodeStr;

                    try {
                        list = ean128Parser.parseBarcodeToList(barcodeStr);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getName().equals("LOT")) {
                                OutOrderDetialActivity.this.lot = list.get(i).getValue();
                                {
                                    setTextById(R.id.LOT, list.get(i).getValue());
                                }
                            }
                            if (list.get(i).getName().equals("expire")) {
                                OutOrderDetialActivity.this.expire = list.get(i).getValue();
                                setTextById(R.id.date, list.get(i).getValue());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "code128":
                    OutOrderDetialActivity.this.barcodeStr = barcodeStr.substring(0, 16);

                    try {
                        list = ean128Parser.parseBarcodeToList(barcodeStr.substring(16));
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getName().equals("LOT")) {
                                OutOrderDetialActivity.this.lot = list.get(i).getValue();
                                setTextById(R.id.LOT, list.get(i).getValue());
                            }
                            if (list.get(i).getName().equals("expire")) {
                                OutOrderDetialActivity.this.expire = list.get(i).getValue();
                                setTextById(R.id.date, list.get(i).getValue());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "HIBC-P":
                    OutOrderDetialActivity.this.barcodeStr = barcodeStr;
                    OutOrderDetialActivity.this.lot = "";
                    OutOrderDetialActivity.this.expire = "";
                    break;
                case "HIBC-S":
//                    OutOrderDetialActivity.this.barcodeStr = barcodeStr;
                    list = hibcParser.HIBCSecondaryParser(barcodeStr);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals("LOT")) {
                            OutOrderDetialActivity.this.lot = list.get(i).getValue();
                            setTextById(R.id.LOT, list.get(i).getValue());
                        }
                        if (list.get(i).getName().equals("expire")) {
                            OutOrderDetialActivity.this.expire = list.get(i).getValue();
                            setTextById(R.id.date, list.get(i).getValue());
                        }
                    }
                    break;
                case "EAN13":
                    break;

                case "hospital-P":
                    OutOrderDetialActivity.this.barcodeStr = barcodeStr.split("\\*")[0];
                    OutOrderDetialActivity.this.lot = barcodeStr.split("\\*")[1];
                    OutOrderDetialActivity.this.expire = "";
                    break;

                case "hospital-S":
//                    OutOrderDetialActivity.this.barcodeStr = barcodeStr.split("\\*")[0];
                    OutOrderDetialActivity.this.expire = barcodeStr.split("\\*")[0];
                    break;
            }
            getProductInfo();
            if (alertDialog != null && alertDialog.isShowing())
                return;
            alertDialog.show();

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanBroadcastReceiver.closeScanManager();
        unregisterReceiver(scanBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseWarehouse:
                Intent intent = new Intent(OutOrderDetialActivity.this, ChooseWareHouseActivity.class);
                intent.putExtra("data", stock);
                startActivityForResult(intent, 1);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String WarehouseName = data.getStringExtra("warehouseName");
                    warehouseId = data.getStringExtra("warehouseId");
                    String LOT = data.getStringExtra("LOT");
                    String expire = data.getStringExtra("expire");
                    String qty = data.getStringExtra("qty");
                    setTextById(R.id.LOT, LOT);
                    setTextById(R.id.expire, expire);
                    setTextById(R.id.textqty, qty);
                    setTextById(R.id.chooseWarehouse, WarehouseName);
                }
                break;
        }
    }

    private void showDialog_Layout(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(windowOut);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);//true表示要关闭
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }


                        outOrders();
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alertDialog.dismiss();

                    }
                });

        alertDialog = builder.create();

    }

}
