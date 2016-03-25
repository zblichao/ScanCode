package com.lichao.scancode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.lichao.scancode.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-15.
 */
public class OutOrderDetialAdapter extends BaseExpandableListAdapter {


    private List<JSONObject> list;
    private Context mContext;
    private LayoutInflater inflater;

    public OutOrderDetialAdapter() {
        super();
    }

    public OutOrderDetialAdapter(Context context) {
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
    public int getGroupCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        JSONObject json = list.get(groupPosition);
        try {
            JSONArray jsonArray = json.getJSONArray("dispatched");
            return jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        JSONObject json = list.get(groupPosition);
        return json;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        JSONObject json = list.get(groupPosition);
        try {
            JSONArray jsonArray = json.getJSONArray("dispatched");
            return jsonArray.getJSONObject(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.list_item_out_order_g, null);
        JSONObject jsonObject = list.get(groupPosition);
        TextView product_name = (TextView) convertView.findViewById(R.id.productName);
        TextView ordered_qty = (TextView) convertView.findViewById(R.id.orderedQty);
        TextView product_size = (TextView) convertView.findViewById(R.id.productSize);
        try {

            product_name.setText(jsonObject.getString("product_name").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            ordered_qty.setText(jsonObject.getString("ordered_qty").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            product_size.setText(jsonObject.getString("product_size").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.list_item_out_order_c, null);

        TextView out = (TextView) convertView.findViewById(R.id.out);
        TextView LOT = (TextView) convertView.findViewById(R.id.LOT);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView warehouse = (TextView) convertView.findViewById(R.id.warehouse);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = list.get(groupPosition).getJSONArray("dispatched").getJSONObject(childPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            out.setText(jsonObject.getString("qty").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            LOT.setText(jsonObject.getString("LOT").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            date.setText(jsonObject.getString("expire").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            warehouse.setText(jsonObject.getString("warehouse_name").replace("null", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
