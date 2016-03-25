package com.lichao.scancode.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lichao.scancode.MyApplication;
import com.lichao.scancode.R;
import com.lichao.scancode.dao.LoginDAO;
import com.lichao.scancode.entity.User;
import com.lichao.scancode.util.CheckNetWorkUtils;
import com.lichao.scancode.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity {

    private Button login;
    private EditText username;
    private EditText password;
    private LoginDAO dao;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        dao = new LoginDAO();
        username.setInputType(InputType.TYPE_NULL);
        username.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText editText = (EditText) v;
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                return true;

            }
        });
        password.setInputType(InputType.TYPE_NULL);
        password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditText editText = (EditText) v;
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                return true;

            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    try {
                        if (res != null && !res.equals("")) {
                            JSONObject resJson = new JSONObject(res);
                            try {
                                if (resJson.getBoolean("login")) {
                                    User user = new User();
                                    user.setPermission(res);
                                    user.setName(username.getText().toString());
                                    user.setPassword(password.getText().toString());
                                    MyApplication.myApplication.setUser(user);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }catch (Exception e)
                            {
                                ToastUtil.showLongToast(LoginActivity.this, "用户名或密码错误");
                            }
                        } else {
                            ToastUtil.showLongToast(LoginActivity.this, "用户名或密码错误");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.showLongToast(LoginActivity.this, "用户名或密码错误");
                    }
                    break;
            }
        }
    };

    private void runLogin() {
        if(!CheckNetWorkUtils.updateConnectedFlags(MyApplication.myApplication))
        {
            ToastUtil.showLongToast(MyApplication.myApplication, "网络不可用");
            return ;
        }
        progressDialog = ProgressDialog.show(this, // context
                "", // title
                "Loading. Please wait...", // message
                true);

        new Thread() {
            @Override
            public void run() {
                super.run();
                res = dao.login(username.getText().toString(), password.getText().toString());
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        }.start();
        ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                runLogin();
                break;
        }
    }
}
