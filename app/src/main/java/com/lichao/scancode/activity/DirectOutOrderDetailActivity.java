package com.lichao.scancode.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.dao.OrderDetailDAO;
import com.lichao.scancode.http.HttpUtil;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.ToastUtil;

public class DirectOutOrderDetailActivity extends BaseActivity {
    private String warehouseId;
    private OrderDetailDAO dao;
    private String res;
    public RadioGroup radioGroup;
    private TabHost mTabHost;
    private RadioButton radioQualified;
    private RadioButton radioDispatched;
    private WebView webView1;
    private WebView webView2;
    private boolean hasload1 = false;
    private  boolean hasload2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_out_order_detail);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Intent intent = getIntent();
        warehouseId = intent.getStringExtra("warehouse_id");

        radioQualified = (RadioButton) findViewById(R.id.radioQualified);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec tabSpec2 = mTabHost.newTabSpec("Qualified");
        tabSpec2.setContent(R.id.tabqualified);
        tabSpec2.setIndicator("Qualified");
        mTabHost.addTab(tabSpec2);
        mTabHost.setCurrentTabByTag("Qualified");
        if(!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication))
        {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return ;
        }


        webView1 = (WebView) findViewById(R.id.web1);
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webView1.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if (!hasload1) {
                        hasload1 = true;
                        webView1.postUrl(HttpUtil.uriAPI + "index.php", ("action=get_direct_out_order&warehouse_id=" + warehouseId).getBytes());
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView1.postUrl(HttpUtil.uriAPI + "index.php", ("action=mobile_login&username=" + MyApplication.myApplication.getUser().getName() + "&password=" + MyApplication.myApplication.getUser().getPassword()).getBytes());
    }


    private void setTextById(int id, String text) {
        TextView textView = (TextView) findViewById(id);
        if (textView != null)
            textView.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
