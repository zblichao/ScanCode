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
public class OrderAdapter extends BaseAdapter {


    private List<JSONObject> list;
    private Context mContext;
    private LayoutInflater inflater;
    public OrderAdapter() {
        super();
    }
    public OrderAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        list = new LinkedList<JSONObject>();

    }
    public List<JSONObject> getList() {
        return list;
    }

    public void setList(List<JSONObject> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(position<list.size())
            return  list.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item_order, null);
        JSONObject jsonObject =list.get(position);
        TextView orderName = (TextView) convertView.findViewById(R.id.orderName);
        TextView orderCompany = (TextView) convertView.findViewById(R.id.orderCompany);
        try {

            orderName.setText(jsonObject.getString("order_name").replace("null",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            orderCompany.setText(jsonObject.getString("supplier_name").replace("null",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
