package com.lichao.scancode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lichao.scancode.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-15.
 */
public class OutOrderAdapter extends BaseAdapter {

    private String filter;
    private List<JSONObject> list;
    private List<JSONObject>listAll;
    private Context mContext;
    private LayoutInflater inflater;

    public OutOrderAdapter() {
        super();
    }

    public OutOrderAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        list = new LinkedList<JSONObject>();
        listAll = new LinkedList<JSONObject>();
    }

    public List<JSONObject> getList() {
        return listAll;
    }

    public void setList(List<JSONObject> list) {
        this.listAll = list;
        setFilter(this.filter);
    }


    public void setFilter(String filter) {
        list.clear();
        this.filter = filter;
        if(filter!=null &&!filter.equals("")) {
            for (int i = 0; i < listAll.size(); i++) {
                try {
                    if (listAll.get(i).getString("order_name").contains(filter))
                        list.add(listAll.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else
        {
            this.list.addAll(this.listAll);
        }
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size())
            return list.get(position);
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item_out_order, null);
        JSONObject jsonObject = list.get(position);
        TextView orderName = (TextView) convertView.findViewById(R.id.orderName);
        TextView orderCompany = (TextView) convertView.findViewById(R.id.orderCompany);
        try {

            orderName.setText(jsonObject.getString("order_name").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            orderCompany.setText(jsonObject.getString("supplier_name").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
