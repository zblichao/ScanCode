package com.lichao.scancode.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.activity.ChooseWarehousesActivity;
import com.lichao.scancode.activity.OrderDetailActivity;
import com.lichao.scancode.dao.QualityTestingFragmentDAO;
import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.receiver.BarcodeReceiver;
import com.lichao.scancode.receiver.EAN128Parser;
import com.lichao.scancode.receiver.HIBCParser;
import com.lichao.scancode.receiver.ScanBroadcastReceiver;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zblichao on 2016-03-10.
 */
public class QualityTestingFragment extends Fragment implements BarcodeReceiver {
    private ScanBroadcastReceiver scanBroadcastReceiver;
    private QualityTestingFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private String allWarehouses;
    private String barcodeStr;
    private Button chooseWarehouse;
    private Button confirm;
    private Button showOrder;
    private String warehousesId;
    private View root;
    private Spinner orders;
    private JSONArray jsonOrders;
    private JSONObject currentOrder;
    private JSONObject jsonProduct;
    private EAN128Parser ean128Parser = new EAN128Parser(); // 你看看放哪儿合适，我一般放在onCreate
    private HIBCParser hibcParser = new HIBCParser();

    public void setScanBroadcastReceiver(ScanBroadcastReceiver scanBroadcastReceiver) {
        this.scanBroadcastReceiver = scanBroadcastReceiver;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_quality_testing, container, false);
        orders = (Spinner) root.findViewById(R.id.orders);
        orders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (jsonOrders != null && jsonOrders.length() < position) {
                    try {
                        currentOrder = jsonOrders.getJSONObject(position);
                        setTextEditTextById(R.id.order_qty, "ordered_qty", currentOrder.getJSONObject("ordered"));
                        setTextEditTextById(R.id.supplier_name, "supplier_name", currentOrder);
                        JSONArray dispatched = currentOrder.getJSONArray("dispatched");
                        int qualified_qty = 0;
                        for (int i = 0; i < dispatched.length(); i++) {
                            qualified_qty += dispatched.getJSONObject(i).getInt("qty");
                        }
                        setTextEditTextById(R.id.qualified_qty, (currentOrder.getJSONObject("ordered").getInt("ordered_qty") - qualified_qty) + "");

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        confirm = (Button) root.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qualify();
            }
        });

        showOrder = (Button) root.findViewById(R.id.show_order);
        showOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOrder != null) {
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    try {
                        intent.putExtra("id", currentOrder.getString("order_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent);
                }
            }
        });
        chooseWarehouse = (Button) root.findViewById(R.id.chooseWarehouse);
        chooseWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChooseWarehousesActivity.class);
                intent.putExtra("data", allWarehouses);
                startActivityForResult(intent, 1);
            }
        });
        dao = new QualityTestingFragmentDAO();
        //getWarehouses();
        //searchProductByCode();
        return root;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String WarehouseName = data.getStringExtra("WarehouseName");
                    chooseWarehouse.setText(WarehouseName);
                    warehousesId = data.getStringExtra("WarehouseId");
                }
                break;
        }
    }

    @Override
    public void onReceiveBarcode(String type, String barcodeStr) {
        ToastUtil.showShortToast(MyApplication.myApplication, type + ":" + barcodeStr);

        ArrayList<NameValuePair> list;
        EditText editText;
        switch (type) {
            case "code128-P":
                this.barcodeStr = barcodeStr;
                setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                if(progressDialog!=null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;
            case "code128-S":
                this.barcodeStr = barcodeStr;
                setTextEditTextById(R.id.product_barcode_secondary, barcodeStr);

                try {
                    list = ean128Parser.parseBarcodeToList(barcodeStr);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals("LOT"))
                            setTextEditTextById(R.id.LOT, list.get(i).getValue());
                        if (list.get(i).getName().equals("expire"))
                            setTextEditTextById(R.id.expire, list.get(i).getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "code128":
                this.barcodeStr = barcodeStr.substring(0, 16);
                setTextEditTextById(R.id.product_barcode_primary, barcodeStr.substring(0, 16));
                setTextEditTextById(R.id.product_barcode_secondary, barcodeStr.substring(16));

                list = hibcParser.HIBCSecondaryParser(barcodeStr.substring(16));
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals("LOT"))
                        setTextEditTextById(R.id.LOT, list.get(i).getValue());
                    if (list.get(i).getName().equals("expire"))
                        setTextEditTextById(R.id.expire, list.get(i).getValue());
                }
                break;
            case "HIBC-P":
                this.barcodeStr = barcodeStr;
                setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                break;
            case "HIBC-S":
                this.barcodeStr = barcodeStr;
                setTextEditTextById(R.id.product_barcode_secondary, barcodeStr);
                list = hibcParser.HIBCSecondaryParser(barcodeStr);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getName().equals("LOT"))
                        setTextEditTextById(R.id.LOT, list.get(i).getValue());
                    if (list.get(i).getName().equals("expire"))
                        setTextEditTextById(R.id.expire, list.get(i).getValue());
                }
                break;
            case "EAN13":
                break;
            case "hospital-P":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                setTextEditTextById(R.id.hospital_barcode_primary, barcodeStr.split("\\*")[0]);
                setTextEditTextById(R.id.LOT, barcodeStr.split("\\*")[1]);
                break;

            case "hospital-S":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                setTextEditTextById(R.id.expire, barcodeStr.split("\\*")[0]);
                searchProductByCode();
                break;
        }


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (res != null && !res.equals("")) {
                        try {
                            JSONObject jsonRes = new JSONObject(res);
                            jsonProduct = jsonRes.getJSONObject("product");
                            allWarehouses = jsonRes.getString("warehouse");
                            setTextEditTextById(R.id.product_barcode_primary, "product_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.product_barcode_secondary, "product_barcode_secondary", jsonProduct);
                            setTextEditTextById(R.id.hospital_barcode_primary, "hospital_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.hospital_barcode_secondary, "hospital_barcode_secondary", jsonProduct);
                            setTextEditTextById(R.id.product_name, "product_name", jsonProduct);
                            setTextEditTextById(R.id.product_huohao, "product_huohao", jsonProduct);
                            setTextEditTextById(R.id.product_fdacode, "product_fdacode", jsonProduct);
                            setTextEditTextById(R.id.product_fdaexpire, "product_fdaexpire", jsonProduct);
                            setTextEditTextById(R.id.product_size, "product_size", jsonProduct);
                            allWarehouses = jsonRes.getString("warehouse");
                            JSONArray jsonArray = new JSONArray(allWarehouses);
                            if (jsonArray.length() > 0) {
                                chooseWarehouse.setText(jsonArray.getJSONObject(0).getString("name"));
                                warehousesId = jsonArray.getJSONObject(0).getString("id");
                            }

                            jsonOrders = jsonRes.getJSONArray("details");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
                            ;
                            for (int i = 0; i < jsonOrders.length(); i++) {
                                adapter.add(jsonOrders.getJSONObject(i).getString("order_name"));
                            }
                            orders.setAdapter(adapter);
                            if (jsonOrders.length() > 0) {
                                currentOrder = jsonOrders.getJSONObject(0);
                                setTextEditTextById(R.id.order_qty, "ordered_qty", currentOrder.getJSONObject("ordered"));
                                setTextEditTextById(R.id.supplier_name, "supplier_name", currentOrder);
                                JSONArray qualified = currentOrder.getJSONArray("qualified");
                                int qualified_qty = 0;
                                for (int i = 0; i < qualified.length(); i++) {
                                    qualified_qty += qualified.getJSONObject(i).getInt("qty");
                                }
                                setTextEditTextById(R.id.qualified_qty, (currentOrder.getJSONObject("ordered").getInt("ordered_qty") - qualified_qty) + "");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 2:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getBoolean("qualify")) {
                            ToastUtil.showLongToast(getContext(), "提交服务器成功");
                            setTextEditTextById(R.id.product_barcode_primary, "");
                            setTextEditTextById(R.id.product_barcode_secondary, "");
                            setTextEditTextById(R.id.hospital_barcode_primary, "");
                            setTextEditTextById(R.id.hospital_barcode_secondary, "");
                            setTextEditTextById(R.id.product_name, "");
                            setTextEditTextById(R.id.product_huohao, "");
                            setTextEditTextById(R.id.product_fdacode, "");
                            setTextEditTextById(R.id.product_fdaexpire, "");
                            setTextEditTextById(R.id.product_size, "");
                            setTextEditTextById(R.id.supplier_name, "");
                            setTextEditTextById(R.id.LOT, "");
                            setTextEditTextById(R.id.expire, "");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
                            orders.setAdapter(adapter);
                            chooseWarehouse.setText("选择仓库");
                            setTextEditTextById(R.id.order_qty, "");
                            setTextEditTextById(R.id.qualified_qty, "");
                        } else {
                            ToastUtil.showLongToast(getContext(), "提交服务器失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showLongToast(getContext(), res);
                    break;
            }
        }
    };

    private void searchProductByCode() {
        if(!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication))
        {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return ;
        }
        progressDialog = ProgressDialog.show(this.getContext(), // context
                "", // title
                "Loading. Please wait...", // message
                true);
        new Thread() {
            @Override
            public void run() {
                super.run();
                res = dao.searchProductByCode(barcodeStr);
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }

    private void qualify() {
        if(!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication))
        {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return ;
        }
        EditText LOTEdit = (EditText) root.findViewById(R.id.LOT);
        final String LOT = LOTEdit.getText().toString();
        if (LOT == null || LOT.equals("")) {
            ToastUtil.showShortToast(getContext(), "请填写LOT");
            return;
        }
        EditText expire = (EditText) root.findViewById(R.id.expire);
        final String dtStart = expire.getText().toString();
        if (dtStart == null || dtStart.equals("")) {
            ToastUtil.showShortToast(getContext(), "请填写LOT过期日期");
            return;
        }
        progressDialog = ProgressDialog.show(this.getContext(), // context
                "", // title
                "Loading. Please wait...", // message
                true);
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {

                    String product_id = jsonProduct.getString("rowid");
                    String det_rowid = currentOrder.getString("det_rowid");
                    String order_id = currentOrder.getString("order_id");
                    String pu = currentOrder.getString("pu");
                    EditText qualifiedEdit = (EditText) root.findViewById(R.id.qualified_qty);
                    String qualified_qty = qualifiedEdit.getText().toString();
                    int ordered_qty = currentOrder.getJSONObject("ordered").getInt("ordered_qty");
                    JSONArray qualified = currentOrder.getJSONArray("qualified");
                    int qualifiedInt = 0;
                    for (int i = 0; i < qualified.length(); i++) {
                        qualifiedInt += qualified.getJSONObject(i).getInt("qty");
                    }
                    String remain_qty = (ordered_qty - qualifiedInt) + "";
                    res = dao.qualify(product_id, dtStart, det_rowid, order_id, pu, qualified_qty, remain_qty, LOT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = 2;
                msg.sendToTarget();
            }
        }.start();

    }

    private EditText setTextEditTextById(int id, String key, JSONObject jsonObject) {
        String text = "";
        try {
            text = jsonObject.getString(key);
        } catch (Exception e) {
        }
        if (text == null || text.equals("null") || text.equals(""))
            return null;
        EditText editText = (EditText) root.findViewById(id);
        editText.setText(text);
        editText.setEnabled(false);
        return editText;
    }

    private EditText setTextEditTextById(int id, String text) {
        if (text == null || text.equals("null"))
            return null;
        EditText editText = (EditText) root.findViewById(id);
        editText.setText(text);
        editText.setEnabled(true);
        return editText;
    }
}
