package com.lichao.scancode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lichao.scancode.R;
import com.lichao.scancode.fragment.InstockFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseWarehousesActivity extends AppCompatActivity {

    private ListView listview;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_warehouses);
        setTitle("库房选择");
        String data = getIntent().getStringExtra("data");
        listview = (ListView) findViewById(R.id.list);
        if (data != null) {
            try {
                jsonArray = new JSONArray(data);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.text);  ;
                for (int i = 0; i < jsonArray.length(); i++) {
                    adapter.add(jsonArray.getJSONObject(i).getString("name"));
                }
                listview.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject json = jsonArray.getJSONObject(position);
                    String warehouse_name = json.getString("name");
                    String warehouse_id = json.getString("id");
                    Intent intent = new Intent(ChooseWarehousesActivity.this, InstockFragment.class);
                    intent.putExtra("WarehouseId",warehouse_id);
                    intent.putExtra("WarehouseName",warehouse_name);
                    setResult(1,intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
