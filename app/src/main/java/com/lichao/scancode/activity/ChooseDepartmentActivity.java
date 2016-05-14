package com.lichao.scancode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.lichao.scancode.R;
import com.lichao.scancode.adapter.DepartmentAdapter;
import com.lichao.scancode.fragment.InstockFragment;
import com.lichao.scancode.util.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChooseDepartmentActivity extends AppCompatActivity {

    private ListView listview;
    private List<JSONObject> jsonArray;
    private SearchView searchView;
    private DepartmentAdapter departmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_department);
        setTitle("库房选择");
        String data = getIntent().getStringExtra("data");
        listview = (ListView) findViewById(R.id.list);
        departmentAdapter= new DepartmentAdapter(this);
        if (data != null) {
            try {
                jsonArray = JSONHelper.JSONArray(data);

                departmentAdapter.setList(jsonArray);
                departmentAdapter.notifyDataSetChanged();
                listview.setAdapter(departmentAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject json = jsonArray.get(position);
                    String  name = json.getString("name");
                    String  ids = json.getString("id");
                    Intent intent = new Intent(ChooseDepartmentActivity.this, InstockFragment.class);
                    intent.putExtra("id",  ids);
                    intent.putExtra("name",  name);
                    setResult(1, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchView= (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                departmentAdapter.filter=newText;
                departmentAdapter.notifyDataSetChanged();
                return false;
            }
        });

    }
}
