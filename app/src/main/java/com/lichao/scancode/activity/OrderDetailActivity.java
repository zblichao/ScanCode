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

public class OrderDetailActivity extends BaseActivity {
    private String id;
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
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioDispatched = (RadioButton) findViewById(R.id.radioDispatched);
        radioQualified = (RadioButton) findViewById(R.id.radioQualified);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec tabSpec1 = mTabHost.newTabSpec("Dispatched");
        tabSpec1.setContent(R.id.tabdispatched);
        tabSpec1.setIndicator("Dispatched");
        mTabHost.addTab(tabSpec1);
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

        webView1.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if(!hasload1)
                    {
                        hasload1 =true;
                        webView1.postUrl(HttpUtil.uriAPI + "index.php?action=get_order_qualified&order_id=" + id, ("username=" + MyApplication.myApplication.getUser().getName() + "&password=" + MyApplication.myApplication.getUser().getPassword()).getBytes());

                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView1.postUrl(HttpUtil.uriAPI + "index.php", ("action=mobile_login&username=" + MyApplication.myApplication.getUser().getName() + "&password=" + MyApplication.myApplication.getUser().getPassword()).getBytes());




        webView2 = (WebView) findViewById(R.id.web2);
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webView2.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    if(!hasload2)
                    {
                        hasload2 =true;
                        webView2.postUrl(HttpUtil.uriAPI + "index.php?action=get_order_dispatched&order_id=" + id, ("username=" + MyApplication.myApplication.getUser().getName() + "&password=" + MyApplication.myApplication.getUser().getPassword()).getBytes());

                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView2.postUrl(HttpUtil.uriAPI + "index.php", ("action=mobile_login&username=" + MyApplication.myApplication.getUser().getName() + "&password" + MyApplication.myApplication.getUser().getPassword()).getBytes());
        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        switch (checkedId) {
                            case R.id.radioDispatched:
                                mTabHost.setCurrentTabByTag("Dispatched");
                                radioQualified.setTextColor(getResources().getColor(R.color.colorPrimary));
                                radioDispatched.setTextColor(getResources().getColor(R.color.colorWhite));
                                radioQualified.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                radioDispatched.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                break;
                            case R.id.radioQualified:
                                mTabHost.setCurrentTabByTag("Qualified");
                                radioQualified.setTextColor(getResources().getColor(R.color.colorWhite));
                                radioDispatched.setTextColor(getResources().getColor(R.color.colorPrimary));
                                radioQualified.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                radioDispatched.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                break;
                        }

                    }
                });
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
