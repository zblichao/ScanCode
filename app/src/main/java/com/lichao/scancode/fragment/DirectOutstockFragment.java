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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.activity.ChooseDepartmentActivity;
import com.lichao.scancode.activity.ChooseWarehousesActivity;
import com.lichao.scancode.dao.DirectOutstockFragmentDAO;
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
public class DirectOutstockFragment extends Fragment implements BarcodeReceiver {
    private ScanBroadcastReceiver scanBroadcastReceiver;
    private DirectOutstockFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private String allWarehouses;
    private String allDepartment;
    private String barcodeStr;
    private Button chooseWarehouse;
    private Button chooseDepartment;
    private Button confirm;
    private Button clearContent;
    private Button showOrder;
    private String warehousesId;
    private String departmentId;
    private String LOT;
    private String expire;
    private View root;
    private JSONArray jsonStock;
    private JSONObject currentStock;
    private JSONObject jsonProduct;
    private EAN128Parser ean128Parser = new EAN128Parser(); // 你看看放哪儿合适，我一般放在onCreate
    private HIBCParser hibcParser = new HIBCParser();

    public void setScanBroadcastReceiver(ScanBroadcastReceiver scanBroadcastReceiver) {
        this.scanBroadcastReceiver = scanBroadcastReceiver;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_direct_outstock, container, false);

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
                setTextEditTextById(R.id.product_barcode_primary, "");
                setTextEditTextById(R.id.product_barcode_secondary, "");
                setTextEditTextById(R.id.hospital_barcode_primary, "");
                setTextEditTextById(R.id.hospital_barcode_secondary, "");
                setTextEditTextById(R.id.product_huohao, "");
                setTextEditTextById(R.id.product_name, "");
                setTextEditTextById(R.id.product_size, "");
                setTextEditTextById(R.id.LOT, "");
                setTextEditTextById(R.id.expire, "");
                setTextEditTextById(R.id.supplier_name, "");
                setTextEditTextById(R.id.product_fdacode, "");
                setTextEditTextById(R.id.product_fdaexpire, "");
                setTextEditTextById(R.id.store_qty, "");
                setTextEditTextById(R.id.out_qty, "");
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

        chooseDepartment = (Button) root.findViewById(R.id.chooseDepartment);
        chooseDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChooseDepartmentActivity.class);
                intent.putExtra("data", allDepartment);
                startActivityForResult(intent, 2);
            }
        });
        dao = new DirectOutstockFragmentDAO();
        getWarehousesAndDepartment();
        warehousesId="";

        return root;
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            EditText out_qty = (EditText) root.findViewById(R.id.out_qty);
            out_qty.requestFocus();

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
            setTextEditTextById(R.id.expire, "");
            setTextEditTextById(R.id.out_qty, "");
            setTextEditTextById(R.id.store_qty, "");
            currentStock = null;
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
            case 2:
                if (data != null) {
                    String name = data.getStringExtra("name");
                    chooseDepartment.setText(name);
                    departmentId = data.getStringExtra("id");
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

                list = hibcParser.HIBCSecondaryParser(barcodeStr.substring(16));
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
                break;
            case "HIBC-P":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;
            case "HIBC-S":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_secondary, barcodeStr);
                editText.setEnabled(false);
                list = hibcParser.HIBCSecondaryParser(barcodeStr);
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
                break;
            case "EAN13":
                this.barcodeStr = barcodeStr;
                editText = setTextEditTextById(R.id.product_barcode_primary, barcodeStr);
                editText.setEnabled(false);
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;
            case "hospital-P":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                editText = setTextEditTextById(R.id.hospital_barcode_primary, barcodeStr.split("\\*")[0]);
                editText.setEnabled(false);
                LOT = barcodeStr.split("\\*")[1];
                if (progressDialog != null && progressDialog.isShowing())
                    return;
                searchProductByCode();
                break;

            case "hospital-S":
                this.barcodeStr = barcodeStr.split("\\*")[0];
                editText = setTextEditTextById(R.id.product_barcode_secondary, barcodeStr);
                editText.setEnabled(false);
                expire = barcodeStr.split("\\*")[0];
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
                            setTextEditTextById(R.id.product_barcode_primary, "product_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.product_barcode_secondary, "product_barcode_secondary", jsonProduct);
                            setTextEditTextById(R.id.hospital_barcode_primary, "hospital_barcode_primary", jsonProduct);
                            setTextEditTextById(R.id.hospital_barcode_secondary, "hospital_barcode_secondary", jsonProduct);
                            setTextEditTextById(R.id.product_name, "product_name", jsonProduct);
                            setTextEditTextById(R.id.product_huohao, "product_huohao", jsonProduct);
                            setTextEditTextById(R.id.product_fdacode, "product_fdacode", jsonProduct);
                            setTextEditTextById(R.id.product_fdaexpire, "product_fdaexpire", jsonProduct);
                            setTextEditTextById(R.id.product_size, "product_size", jsonProduct);
                            setTextEditTextById(R.id.supplier_name, "manufacture_name", jsonProduct);

                            jsonStock = jsonRes.getJSONArray("stock");
                            if (jsonStock.length() > 0) {
                                for (int i = 0; i < jsonStock.length(); i++)
                                    if (jsonStock.getJSONObject(i).getString("LOT").equals(LOT)) {
                                        EditText editText;
                                        editText = setTextEditTextById(R.id.LOT, LOT);
                                        editText.setEnabled(false);
                                        editText = setTextEditTextById(R.id.expire, jsonStock.getJSONObject(i).getString("expire").toString());
                                        editText.setEnabled(false);
                                        editText = setTextEditTextById(R.id.store_qty, jsonStock.getJSONObject(i).getString("qty").toString());
                                        editText.setEnabled(false);
                                    }
                            }
                            EditText editText = setTextEditTextById(R.id.out_qty, "");
                            CharSequence text = editText.getText();
                            editText.setSelection(text.length());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case 2:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

//                    ToastUtil.showShortToast(getContext(), res);
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getBoolean("dispatch")) {
                            ToastUtil.showShortToast(getContext(), "提交服务器成功");
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
                            setTextEditTextById(R.id.store_qty, "");
                            setTextEditTextById(R.id.out_qty, "");
                            currentStock = null;
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
                res = dao.searchProductByCode(barcodeStr, warehousesId);
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }


    private void getWarehousesAndDepartment() {
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
                allDepartment = dao.getDepartment();
                try {
                    allDepartment = new JSONObject(allDepartment).getString("deparment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.arg1 = 3;
                msg.sendToTarget();
            }
        }.start();
    }

    private void instock() {
        if (!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "网络不可用");
            return;
        }

        if (warehousesId == null) {
            ToastUtil.showShortToast(MyApplication.myApplication, "请选择出库库房");
            return;
        }

        if (departmentId == null) {
            ToastUtil.showShortToast(MyApplication.myApplication, "请选择科室");
            return;
        }

        if (((EditText) root.findViewById(R.id.product_name)).getText().toString().length() == 0) {
            ToastUtil.showShortToast(MyApplication.myApplication, "请扫码");
            return;
        }

        if ((((EditText) root.findViewById(R.id.LOT)).getText().toString().length() == 0) ||
                (((EditText) root.findViewById(R.id.expire)).getText().toString().length() == 0)) {
            ToastUtil.showShortToast(MyApplication.myApplication, "未找到库存");
            return;
        }

        if (((EditText) root.findViewById(R.id.out_qty)).getText().toString().length() == 0) {
            ToastUtil.showShortToast(MyApplication.myApplication, "请填写数量");
            return;
        }

        try {
            EditText dispatchedEdit = (EditText) root.findViewById(R.id.out_qty);
            String dispatched_qty = dispatchedEdit.getText().toString();
            int ordered_qty = currentStock.getInt("qty");
            int qualifiedInt = 0;
            int dispatched = Integer.parseInt(dispatched_qty);

            if (dispatched > ordered_qty) {
                ToastUtil.showShortToast(getContext(), "出库数量不能大于库存数量");
                return;
            }

        } catch (Exception e) {
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
                    EditText expire = (EditText) root.findViewById(R.id.expire);
                    String dtStart = expire.getText().toString();
                    EditText LOTEdit = (EditText) root.findViewById(R.id.LOT);
                    String LOT = LOTEdit.getText().toString();
                    EditText out_qty = (EditText) root.findViewById(R.id.out_qty);
                    String ordered_qty = out_qty.getText().toString();
                    res = dao.outOrders(departmentId, product_id, ordered_qty, LOT , dtStart,  warehousesId);
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
