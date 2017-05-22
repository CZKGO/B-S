package com.czk.diabetes.signin;

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
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class SignActivity extends BaseActivity {
    private static final int HANDLER_NET_ERROR = -1;

    private ImageView ivIcon;
    private ImageView ivCode;
    private String realCode;
    private EditText etName;
    private EditText etPwd;
    private EditText etCode;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_NET_ERROR:
                    ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        initView();
        dealEvent();
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.sign_in));
        TextView tvRight = (TextView) findViewById(R.id.tv_right);
        tvRight.setText(getResources().getString(R.string.register));
        //主体
        ImageView ivIconName = (ImageView) findViewById(R.id.icon_name);
        ivIconName.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_user));
        ImageView ivIconPassword = (ImageView) findViewById(R.id.icon_password);
        ivIconPassword.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_lock_rounded));

        ivCode = (ImageView) findViewById(R.id.img_code);
        //将验证码用图片的形式显示出来
        ivCode.setImageBitmap(VerificationCode.getInstance().setSize(DimensUtil.dpTopx(SignActivity.this, 60), DimensUtil.dpTopx(SignActivity.this, 24)).createBitmap());
        realCode = VerificationCode.getInstance().getCode().toLowerCase();

        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etCode = (EditText) findViewById(R.id.et_code);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.bt_singn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
    }

    private void checkUser() {
        if (StringUtil.isEmpty(etName.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.user_name_not_null));
        } else if (StringUtil.isEmpty(etPwd.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.pwd_not_null));
        } else if (StringUtil.isEmpty(etCode.getText().toString()) && !realCode.equals(etCode.getText().toString().toLowerCase())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.code_error));
            refreshCode();
        } else {
            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("checkLogIn")
                    , DiabetesClient.checkLogIn(etName.getText().toString(), etPwd.getText().toString())
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            int result = new Integer(new String(responseBody));
                            switch (result){
                                case 0:
                                    startActivity(new Intent(SignActivity.this, MainActivity.class));
                                    break;
                                case 1:
                                    ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.user_name_or_pwd_error));
                                    break;
                                default:
                                    handler.sendEmptyMessage(HANDLER_NET_ERROR);
                                    break;
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
