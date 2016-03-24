package com.lichao.scancode.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.fragment.CheckOrderFragment;
import com.lichao.scancode.fragment.InstockFragment;
import com.lichao.scancode.fragment.OutstockFragment;
import com.lichao.scancode.fragment.QualityTestingFragment;
import com.lichao.scancode.receiver.ScanBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CheckOrderFragment checkOrderFragment;
    private InstockFragment instockFragment;
    private OutstockFragment outstockFragment;
    private QualityTestingFragment qualityTestingFragment;
    private Toolbar toolbar;
    private String currentMenuItem;
    public  final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("质检");

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
       // navigationView.inflateMenu(R.menu.activity_main_drawer);
        try {
            JSONObject permission = new JSONObject(MyApplication.myApplication.getUser().getPermission());
            if(permission.getBoolean("qualify"))
            navigationView.getMenu().add("质检");
            //if(permission.getBoolean(""))
            navigationView.getMenu().add("入库");
            // if(permission.getBoolean(""))

            navigationView.getMenu().add("出库");

            //if(permission.getBoolean(""))
            navigationView.getMenu().add("查看订单");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        checkOrderFragment = new CheckOrderFragment();
        instockFragment = new InstockFragment();
        instockFragment.setScanBroadcastReceiver(scanBroadcastReceiver);
        outstockFragment = new OutstockFragment();
        qualityTestingFragment = new QualityTestingFragment();
        qualityTestingFragment.setScanBroadcastReceiver(scanBroadcastReceiver);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, checkOrderFragment)
                .add(R.id.fragment_container, instockFragment)
                .add(R.id.fragment_container, outstockFragment)
                .add(R.id.fragment_container, qualityTestingFragment)
                .hide(instockFragment)
                .hide(outstockFragment)
                .show(qualityTestingFragment)
                .hide(checkOrderFragment)
                .commit();

        currentMenuItem = "质检";

        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(scanBroadcastReceiver, filter);
        //scanBroadcastReceiver.initScanManger();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanBroadcastReceiver.closeScanManager();
        unregisterReceiver(scanBroadcastReceiver);
    }

    private ScanBroadcastReceiver scanBroadcastReceiver = new ScanBroadcastReceiver() {
        @Override
        public void onReceiveBarcode(String type, String barcodeStr) {
            switch (currentMenuItem) {
                case "查看订单":
                     break;
                case "出库":
                    break;
                case "入库":
                    instockFragment.onReceiveBarcode(type, barcodeStr);
                    break;
                case "质检":
                    qualityTestingFragment.onReceiveBarcode(type, barcodeStr);
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String id = item.getTitle().toString();

        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        currentMenuItem = id;
        switch (id) {
            case "查看订单":
                trx.hide(instockFragment);
                trx.hide(outstockFragment);
                trx.hide(qualityTestingFragment);
                trx.show(checkOrderFragment);
                toolbar.setTitle("查看订单");
                break;
            case "出库":
                trx.hide(instockFragment);
                trx.show(outstockFragment);
                trx.hide(qualityTestingFragment);
                trx.hide(checkOrderFragment);
                toolbar.setTitle("出库");
                break;
            case "入库":
                trx.show(instockFragment);
                trx.hide(outstockFragment);
                trx.hide(qualityTestingFragment);
                trx.hide(checkOrderFragment);
                toolbar.setTitle("入库");
                break;
            case "质检":
                trx.hide(instockFragment);
                trx.hide(outstockFragment);
                trx.show(qualityTestingFragment);
                trx.hide(checkOrderFragment);
                toolbar.setTitle("质检");
                break;
        }
        trx.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
