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
import com.lichao.scancode.dao.CountingFragmentDAO;
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
public class CountingFragment extends Fragment implements BarcodeReceiver {
    private ScanBroadcastReceiver scanBroadcastReceiver;
    private CountingFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private String allWarehouses;
    private String barcodeStr;
    private Button chooseWarehouse;
    private Button confirm;
    private Button clearContent;
    private Spinner plusMinus;
    private Button showOrder;
    private int movement;
    private String warehousesId;
    private View root;
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
        root = inflater.inflate(R.layout.fragment_counting, container, false);

        plusMinus = (Spinner) root.findViewById(R.id.plus_minus);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
        adapter.add("增加库存");
        adapter.add("减少库存");
        plusMinus.setAdapter(adapter);

        plusMinus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                movement = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirm = (Button) root.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instock();
            }
        });
        clearContent = (Button) root.findViewById(R.id.clear);
        clearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextEditTextById(R.id.hospital_barcode_primary, "");
                setTextEditTextById(R.id.hospital_barcode_secondary, "");
                setTextEditTextById(R.id.product_name, "");
                setTextEditTextById(R.id.product_size, "");
                setTextEditTextById(R.id.product_fdacode, "");
                setTextEditTextById(R.id.product_fdaexpire, "");
                setTextEditTextById(R.id.dispatched_qty, "");
                setTextEditTextById(R.id.LOT, "");
                setTextEditTextById(R.id.expire, "");
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
        dao = new CountingFragmentDAO();
        getWarehouses();
        //searchProductByCode();

        return root;
    }
    private void getWarehouses() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "网络不可用");
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                allWarehouses = dao.getWarehouses();
                try {
                    allWarehouses = new JSONObject(allWarehouses).getString("warehouse");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = 3;
                msg.sendToTarget();

            }
        }.start();

    }
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            EditText dispatched_qty = (EditText) root.findViewById(R.id.dispatched_qty);
            dispatched_qty.requestFocus();

            setTextEditTextById(R.id.hospital_barcode_primary, "");
            setTextEditTextById(R.id.hospital_barcode_secondary, "");
            setTextEditTextById(R.id.product_name, "");
            setTextEditTextById(R.id.product_fdacode, "");
            setTextEditTextById(R.id.product_fdaexpire, "");
            setTextEditTextById(R.id.product_size, "");
            setTextEditTextById(R.id.LOT, "");
            setTextEditTextById(R.id.expire, "");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);

            setTextEditTextById(R.id.dispatched_qty, "");
            currentOrder = null;
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
        //ToastUtil.showShortToast(MyApplication.myApplication, type + ":" + barcodeStr);

        ArrayList<NameValuePair> list;
        EditText editText;
        switch (type) {
            case "hospital-P":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                editText = setTextEditTextById(R.id.hospital_barcode_primary, barcodeStr.split("\\*")[0]);
                editText.setEnabled(false);
                editText = setTextEditTextById(R.id.LOT, barcodeStr.split("\\*")[1]);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;

            case "hospital-S":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                editText = setTextEditTextById(R.id.expire, barcodeStr.split("\\*")[0]);
                editText.setEnabled(false);
                editText = setTextEditTextById(R.id.hospital_barcode_secondary, barcodeStr);
                editText.setEnabled(false);
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

                            setTextEditTextById(R.id.hospital_barcode_primary, "hospital_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.hospital_barcode_secondary, "hospital_barcode_secondary", jsonProduct);
                            setTextEditTextById(R.id.product_name, "product_name", jsonProduct);
                            setTextEditTextById(R.id.product_fdacode, "product_fdacode", jsonProduct);
                            setTextEditTextById(R.id.product_fdaexpire, "product_fdaexpire", jsonProduct);
                            setTextEditTextById(R.id.product_size, "product_size", jsonProduct);

                            jsonOrders = jsonRes.getJSONArray("details");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
                            ;
                            for (int i = 0; i < jsonOrders.length(); i++) {
                                adapter.add(jsonOrders.getJSONObject(i).getString("order_name"));
                            }
                            if (jsonOrders.length() > 0) {
                                currentOrder = jsonOrders.getJSONObject(0);
                                JSONArray qualified = currentOrder.getJSONArray("qualified");
                                if (qualified.length() > 0) {
                                    JSONObject qualifyDetial = qualified.getJSONObject(0);
                                    setTextEditTextById(R.id.LOT, "LOT", qualifyDetial);
                                    setTextEditTextById(R.id.expire, "expire", qualifyDetial);
                                }

                                JSONArray dispatched = currentOrder.getJSONArray("dispatched");
                                int dispatched_qty = 0;
                                for (int i = 0; i < dispatched.length(); i++) {
                                    dispatched_qty += dispatched.getJSONObject(i).getInt("qty");
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 2:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

//                    ToastUtil.showShortToast(getContext(),res );
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getBoolean("dispatch")) {
                            ToastUtil.showShortToast(getContext(), "提交服务器成功");
                            setTextEditTextById(R.id.hospital_barcode_primary, "");
                            setTextEditTextById(R.id.hospital_barcode_secondary, "");
                            setTextEditTextById(R.id.product_name, "");
                            setTextEditTextById(R.id.product_fdacode, "");
                            setTextEditTextById(R.id.product_fdaexpire, "");
                            setTextEditTextById(R.id.product_size, "");
                            setTextEditTextById(R.id.LOT, "");
                            setTextEditTextById(R.id.expire, "");
                            setTextEditTextById(R.id.dispatched_qty, "");
                            currentOrder = null;
                        } else {
                            ToastUtil.showShortToast(getContext(), "提交服务器失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    ToastUtil.showShortToast(getContext(), res);
                    break;
                case 3:
                    try {
                        JSONArray jsonArray = new JSONArray(allWarehouses);
                        if (jsonArray.length() > 0) {
                            warehousesId = jsonArray.getJSONObject(0).getString("id");
                            chooseWarehouse.setText(jsonArray.getJSONObject(0).getString("name"));
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };

    private void searchProductByCode() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "网络不可用");
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
                res = dao.searchProductByCode(barcodeStr);
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }

    private void instock() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "网络不可用");
            return;
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
        EditText qty = (EditText) root.findViewById(R.id.dispatched_qty);
        final String qty_string = qty.getText().toString();
        if (qty_string == null || qty_string.equals("")) {
            ToastUtil.showShortToast(getContext(), "请填写数量");
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
                String barcode = ((EditText) root.findViewById(R.id.hospital_barcode_primary)).getText().toString();
                String LOT = ((EditText) root.findViewById(R.id.LOT)).getText().toString();
                String expire = ((EditText) root.findViewById(R.id.expire)).getText().toString();
                String qty = ((EditText) root.findViewById(R.id.dispatched_qty)).getText().toString();
                res = dao.instock(barcode, LOT, expire, warehousesId, qty, movement);

                System.out.println(res);

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
