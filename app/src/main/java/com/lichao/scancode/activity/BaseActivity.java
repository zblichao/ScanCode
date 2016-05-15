package com.lichao.scancode.activity;

import android.app.ProgressDialog;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.dao.LoginDAO;
import com.lichao.scancode.entity.User;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.SharedPreferencesUtil;
import com.lichao.scancode.util.StreamTools;
import com.lichao.scancode.util.ToastUtil;

/**
 * Created by zblichao on 2016-03-10.
 */
public  abstract  class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected ProgressDialog progressDialog;
    private LoginDAO dao1;
    private User u;

    @Override
    protected void onResume() {
        super.onResume();
        dao1 = new LoginDAO();
        User u = SharedPreferencesUtil.getUser(this);
        if(u!=null&&u.getUserName()!=null&&u.getPassword()!=null&&!u.getUserName().equals("")&&!u.getPassword().equals(""))
        {
            runLogin();
        }
    }
    private void runLogin() {
        if(!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication))
        {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return ;
        }


        new Thread() {
            @Override
            public void run() {
                super.run();
                 dao1.login(u.getUserName(), u.getPassword());

            }
        }.start();

    }

}
