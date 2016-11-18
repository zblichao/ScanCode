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
import com.lichao.scancode.dao.CorrespondenceFragmentDAO;
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
public class CorrespondenceFragment extends Fragment implements BarcodeReceiver {
    private ScanBroadcastReceiver scanBroadcastReceiver;
    private CorrespondenceFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private String allWarehouses;
    private String barcodeStr;
    private Button chooseWarehouse;
    private Button confirm;
    private Button clearContent;
    private String warehousesId;
    private View root;
    private JSONArray jsonOrders;
    private JSONObject currentOrder;
    private JSONObject jsonProduct;
    private EAN128Parser ean128Parser = new EAN128Parser();
    private HIBCParser hibcParser = new HIBCParser();

    public void setScanBroadcastReceiver(ScanBroadcastReceiver scanBroadcastReceiver) {
        this.scanBroadcastReceiver = scanBroadcastReceiver;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_correspondence, container, false);
        confirm = (Button) root.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qualify();
            }
        });

        clearContent = (Button) root.findViewById(R.id.clear);
        clearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextEditTextById(R.id.product_barcode_primary, "");
                setTextEditTextById(R.id.hospital_barcode_primary, "");
                setTextEditTextById(R.id.product_name, "");
                setTextEditTextById(R.id.product_size, "");
                setTextEditTextById(R.id.product_fdacode, "");
                setTextEditTextById(R.id.product_fdaexpire, "");
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
        dao = new CorrespondenceFragmentDAO();
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
            setTextEditTextById(R.id.product_barcode_primary, "");
            setTextEditTextById(R.id.hospital_barcode_primary, "");
            setTextEditTextById(R.id.product_name, "");
            setTextEditTextById(R.id.product_fdacode, "");
            setTextEditTextById(R.id.product_fdaexpire, "");
            setTextEditTextById(R.id.product_size, "");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
            chooseWarehouse.setText("选择仓库");
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
//        ToastUtil.showShortToast(MyApplication.myApplication, type + ":" + barcodeStr);

        ArrayList<NameValuePair> list;
        EditText editText;
        switch (type) {
            case "code128-P":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;
            case "code128-S":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_secondary, barcodeStr);
                editText.setEnabled(false);
                try {
                    list = ean128Parser.parseBarcodeToList(barcodeStr);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals("LOT")) {
                            editText = setTextEditTextById(R.id.LOT, list.get(i).getValue());
                            editText.setEnabled(false);
                        }
                        if (list.get(i).getName().equals("expire")) {
                            editText = setTextEditTextById(R.id.expire, list.get(i).getValue());
                            editText.setEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "code128":
                this.barcodeStr = barcodeStr.substring(0, 16);
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr.substring(0, 16));
                editText.setEnabled(false);
                editText = setTextEditTextById(R.id.product_barcode_secondary, barcodeStr.substring(16));
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();

//                list = hibcParser.HIBCSecondaryParser(barcodeStr.substring(16));
                try {
                    list = ean128Parser.parseBarcodeToList(barcodeStr.substring(16));
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getName().equals("LOT")) {
                            editText = setTextEditTextById(R.id.LOT, list.get(i).getValue());
                            editText.setEnabled(false);
                        }
                        if (list.get(i).getName().equals("expire")) {
                            editText = setTextEditTextById(R.id.expire, list.get(i).getValue());
                            editText.setEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "HIBC-P":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
//                searchProductByCode();
                break;
            case "HIBC-S":
                break;
            case "EAN13":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
//                searchProductByCode();
                break;
            case "hospital-P":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                editText = setTextEditTextById(R.id.hospital_barcode_primary, barcodeStr.split("\\*")[0]);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;

            case "hospital-S":
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
                            setTextEditTextById(R.id.hospital_barcode_primary, "hospital_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.product_name, "product_name", jsonProduct);
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 2:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

//                    System.out.println(res);

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getBoolean("qualify")) {
                            ToastUtil.showShortToast(getContext(), "提交服务器成功");
                            setTextEditTextById(R.id.product_barcode_primary, "");
                            setTextEditTextById(R.id.hospital_barcode_primary, "");
                            setTextEditTextById(R.id.product_name, "");
                            setTextEditTextById(R.id.product_fdacode, "");
                            setTextEditTextById(R.id.product_fdaexpire, "");
                            setTextEditTextById(R.id.product_size, "");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, R.id.text);
                            chooseWarehouse.setText("选择仓库");
                            currentOrder = null;
                        } else {
                            ToastUtil.showShortToast(getContext(), "提交服务器失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    ToastUtil.showShortToast(getContext(), res);
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

    private void qualify() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "网络不可用");
            return;
        }
        EditText productEdit = (EditText) root.findViewById(R.id.product_barcode_primary);
        final String productBarcode = productEdit.getText().toString();
        if (productBarcode == null || productBarcode.equals("")) {
            ToastUtil.showShortToast(getContext(), "请扫描产品主码");
            return;
        }
        EditText hospitalEdit = (EditText) root.findViewById(R.id.hospital_barcode_primary);
        final String hospitalBarcode = hospitalEdit.getText().toString();
        if (hospitalBarcode == null || hospitalBarcode.equals("")) {
            ToastUtil.showShortToast(getContext(), "请扫描院内主码");
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
                res = dao.qualify(productBarcode, hospitalBarcode);

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
