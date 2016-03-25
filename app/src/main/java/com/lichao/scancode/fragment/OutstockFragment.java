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
import android.widget.ListView;
import android.widget.SearchView;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.activity.OutOrderDetialActivity;
import com.lichao.scancode.adapter.OutOrderAdapter;
import com.lichao.scancode.dao.OutstockFragmentDAO;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.JSONHelper;
import com.lichao.scancode.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zblichao on 2016-03-10.
 */
public class OutstockFragment extends Fragment    implements
        SearchView.OnQueryTextListener {
    private OutstockFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private View root;
    private ListView list;
    private OutOrderAdapter outOrderAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_outstock, container, false);
        list = (ListView) root.findViewById(R.id.list);
        outOrderAdapter = new OutOrderAdapter(getContext());
        list.setAdapter(outOrderAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject order = outOrderAdapter.getList().get(position);
                Intent intent = new Intent(getContext(), OutOrderDetialActivity.class);
                try {
                    intent.putExtra("order_id",order.getString("order_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });
        list.setTextFilterEnabled(true);

        searchView = (SearchView) root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        dao = new OutstockFragmentDAO();
        return root;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
            getOutOrders();
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

                    try {
                        List<JSONObject> temp = JSONHelper.JSONArray(res);
                        outOrderAdapter.setList(temp);
                        outOrderAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void getOutOrders() {
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
                res = dao.getOutOrders();
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query == null || query.equals("")) {
            // Clear the text filter.
            outOrderAdapter.setFilter("");
        } else {
            // Sets the initial value for the text filter.
            outOrderAdapter.setFilter(query);

        }
        outOrderAdapter.notifyDataSetChanged();
        return false;
    }
}
