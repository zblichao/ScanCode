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
import java.util.concurrent.Exchanger;

/**
 * Created by zblichao on 2016-03-15.
 */
public class DepartmentAdapter extends BaseAdapter {


    public String filter;
    private List<JSONObject> list;
    private List<JSONObject> listShow;
    private Context mContext;
    private LayoutInflater inflater;
    public DepartmentAdapter() {
        super();
    }
    public DepartmentAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        list = new LinkedList<JSONObject>();
        listShow = new LinkedList<JSONObject>();
        filter="";
    }
    public List<JSONObject> getList() {
        return list;
    }

    public void setList(List<JSONObject> list) {
        this.list = list;
    }

    @Override
    public void notifyDataSetChanged() {
        listShow.clear();
        for (JSONObject json :
                list) {
            try {
                if (filter.equals("")||json.getString("pinyin").contains(filter))
                    listShow.add(json);
            }catch (Exception e){}
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (listShow != null  )
            return listShow.size();
       else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(position<listShow.size())
            return  listShow.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item, null);
        JSONObject jsonObject =listShow.get(position);
        TextView text = (TextView) convertView.findViewById(R.id.text);
         try {

             text.setText(jsonObject.getString("name").replace("null",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }
}
