package com.lichao.scancode.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lichao.scancode.R;
import com.lichao.scancode.activity.OrderDetailActivity;
import com.lichao.scancode.adapter.OrderAdapter;
import com.lichao.scancode.dao.CheckOrderFragmentDAO;
import com.lichao.scancode.util.JSONHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by zblichao on 2016-03-10.
 */
public class CheckOrderFragment extends Fragment {

    private CheckOrderFragmentDAO dao;
    private ProgressDialog progressDialog;
    private String res;
    private View root;
    private ListView listOrder;
    private OrderAdapter orderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  return super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_check_order, container, false);
        listOrder = (ListView) root.findViewById(R.id.list);
        orderAdapter = new OrderAdapter(getContext());
        listOrder.setAdapter(orderAdapter);
        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject json = orderAdapter.getList().get(position);
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                try {
                    intent.putExtra("id", json.getString("order_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        dao = new CheckOrderFragmentDAO();
        //getOrders();
        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            getOrders();
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
                        orderAdapter.setList(temp);
                        orderAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };


    private void getOrders() {

        progressDialog = ProgressDialog.show(this.getContext(), // context
                "", // title
                "Loading. Please wait...", // message
                true);

        new Thread() {
            @Override
            public void run() {
                super.run();
                res = dao.getOrders();
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();

    }


}

