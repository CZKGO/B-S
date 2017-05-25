package com.czk.diabetes.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.MainActivity;
import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.dialog.LoadingDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class LogInActivity extends BaseActivity {
    private static final int HANDLER_NET_ERROR = -1;
    private ImageView ivCode;
    private String realCode;
    private EditText etName;
    private EditText etPwd;
    private EditText etCode;

    private int lastSingIn = -1;//0表示登录成功，1表示登录失败，-1表示还未登录完毕

    private View layoutSignIn;
    private View layoutSplash;
    private LoadingDialog loadingDialog;
    private long loadingStartTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_NET_ERROR:
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.server_time_out));
                            }
                        }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                    } else {
                        ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.server_time_out));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();
        checkLastUser();
        dealEvent();
    }

    private void checkLastUser() {
        String lastName = MyApplication.getInstance()
                .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .getString(SharedPreferencesUtils.USER_NAME, null);
        String lastPwd = MyApplication.getInstance()
                .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .getString(SharedPreferencesUtils.USER_PWD, "null");
        if (null == lastName) {
            lastSingIn = 1;
        } else {
            lastSingIn = 0;
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
//            重新验证代码
//            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("checkLogIn")
//                    , DiabetesClient.checkLogIn(lastName, lastPwd)
//                    , new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            try {
//                                JSONObject jsonObject = StringUtil.readJsonFromInputStream(new ByteArrayInputStream(responseBody));
//                                switch (jsonObject.getInt("code")) {
//                                    case 0:
//                                        lastSingIn = 0;
//                                        break;
//                                    case 1:
//                                    default:
//                                        lastSingIn = 1;
//                                        break;
//                                }
//                            } catch (Exception e) {
//                                lastSingIn = 1;
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                            handler.sendEmptyMessage(HANDLER_NET_ERROR);
//                        }
//                    });
        }
    }

    private void initView() {
        //主体
        ImageView ivIconName = (ImageView) findViewById(R.id.icon_name);
        ivIconName.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_user));
        ImageView ivIconPassword = (ImageView) findViewById(R.id.icon_password);
        ivIconPassword.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_lock_rounded));

        ivCode = (ImageView) findViewById(R.id.img_code);
        //将验证码用图片的形式显示出来
        ivCode.setImageBitmap(VerificationCode.getInstance().setSize(DimensUtil.dpTopx(LogInActivity.this, 60), DimensUtil.dpTopx(LogInActivity.this, 24)).createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();

        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etCode = (EditText) findViewById(R.id.et_code);

        layoutSignIn = findViewById(R.id.sign_layout);
        layoutSplash = findViewById(R.id.splash_layout);

        TextView textView = (TextView) findViewById(R.id.phone_type);
        textView.setText(android.os.Build.MODEL);

        loadingDialog = new LoadingDialog(LogInActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void dealEvent() {
        findViewById(R.id.bt_singn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });

        findViewById(R.id.bt_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == lastSingIn) {
                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    finish();
                } else if (1 == lastSingIn) {
                    layoutSplash.setVisibility(View.GONE);
                    layoutSignIn.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.bt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, SignActivity.class));
                finish();
            }
        });
    }

    private void checkUser() {
        if (StringUtil.isEmpty(etName.getText().toString())) {
            ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.user_name_not_null));
        } else if (StringUtil.isEmpty(etPwd.getText().toString())) {
            ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.pwd_not_null));
        } else if (StringUtil.isEmpty(etCode.getText().toString()) || !realCode.equals(etCode.getText().toString().toLowerCase())) {
            ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.code_error));
            refreshCode();
        } else {
            if (null != loadingDialog) {
                loadingDialog.show();
                loadingStartTime = System.currentTimeMillis();
            }
            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("checkLogIn")
                    , DiabetesClient.checkLogIn(etName.getText().toString(), etPwd.getText().toString())
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                JSONObject jsonObject = StringUtil.readJsonFromInputStream(new ByteArrayInputStream(responseBody));
                                switch (jsonObject.getInt("code")) {
                                    case 0:
                                        MyApplication.getInstance()
                                                .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                                                .edit()
                                                .putString(SharedPreferencesUtils.USER_NAME, etName.getText().toString()).commit();
                                        MyApplication.getInstance()
                                                .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                                                .edit()
                                                .putString(SharedPreferencesUtils.USER_PWD, etPwd.getText().toString()).commit();
                                        if (null != loadingDialog) {
                                            long loadingTime = System.currentTimeMillis() - loadingStartTime;
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                                    finish();
                                                    loadingDialog.dismiss();
                                                }
                                            }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                                        } else {
                                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        break;
                                    case 1:
                                    default:
                                        if (null != loadingDialog) {
                                            long loadingTime = System.currentTimeMillis() - loadingStartTime;
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.user_name_or_pwd_error));
                                                    loadingDialog.dismiss();
                                                }
                                            }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                                        } else {
                                            ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.user_name_or_pwd_error));
                                        }
                                        break;
                                }
                            } catch (Exception e) {
                                if (null != loadingDialog) {
                                    long loadingTime = System.currentTimeMillis() - loadingStartTime;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.user_name_or_pwd_error));
                                            loadingDialog.dismiss();
                                        }
                                    }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                                } else {
                                    ToastUtil.showShortToast(LogInActivity.this, getResources().getString(R.string.user_name_or_pwd_error));
                                }
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            handler.sendEmptyMessage(HANDLER_NET_ERROR);
                        }
                    });
        }

    }

    private void refreshCode() {
        ivCode.setImageBitmap(VerificationCode.getInstance().createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();
    }
}
