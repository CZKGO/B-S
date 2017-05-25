package com.czk.diabetes.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.CameraUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ToastUtil;

/**
 * Created by 陈忠凯 on 2017/5/25.
 */

public class SignActivity extends BaseActivity {
    private final static int CODE_CHOSE_IMAGE = 1024;
    private final static int CODE_RESULT_REQUEST = 1025;
    private ImageView ivIcon;
    private View layoutMan;
    private View layoutWoman;
    private ImageView ivMan;
    private ImageView ivWoman;
    private TextView tvMan;
    private TextView tvWoman;
    private View layoutHead;
    private ImageView ivHead;
    private EditText etName;
    private EditText etPwd;
    private EditText etAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        initView();
        dealEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

//        // 用户没有进行有效的设置操作，返回
//        if (resultCode == RESULT_CANCELED) {
//            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
//            return;
//        }

        switch (requestCode) {
            case CODE_CHOSE_IMAGE:
                CameraUtil.cropRawPhoto(SignActivity.this,intent.getData(),CODE_RESULT_REQUEST);
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    ivHead.setImageBitmap(CameraUtil.setImageToHeadView(intent));
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.sidn_in));

        /**主体**/
        layoutHead = findViewById(R.id.head_layout);
        ivHead = (ImageView) findViewById(R.id.iv_head);

        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etAge = (EditText) findViewById(R.id.et_age);

        layoutMan = findViewById(R.id.man_layout);
        ivMan = (ImageView) findViewById(R.id.man_iv);
        tvMan = (TextView) findViewById(R.id.man_text);

        layoutWoman = findViewById(R.id.woman_layout);
        ivWoman = (ImageView) findViewById(R.id.woman_iv);
        tvWoman = (TextView) findViewById(R.id.woman_text);

        FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
        iconMan.setTextColor(getResources().getColor(R.color.safe_color));
        ivMan.setImageDrawable(iconMan);
        tvMan.setTextColor(getResources().getColor(R.color.safe_color));

        FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
        iconWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
        ivWoman.setImageDrawable(iconWoman);
    }


    private void dealEvent() {
        //头部
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.choseHeadImageFromGallery(SignActivity.this, CODE_CHOSE_IMAGE);
            }
        });

        layoutMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconMan.setTextColor(getResources().getColor(R.color.safe_color));
                ivMan.setImageDrawable(iconMan);
                tvMan.setTextColor(getResources().getColor(R.color.safe_color));

                FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
                ivWoman.setImageDrawable(iconWoman);
                tvWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
            }
        });

        layoutWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconMan.setTextColor(getResources().getColor(R.color.txt_light_color));
                ivMan.setImageDrawable(iconMan);
                tvMan.setTextColor(getResources().getColor(R.color.txt_light_color));

                FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconWoman.setTextColor(getResources().getColor(R.color.safe_color));
                ivWoman.setImageDrawable(iconWoman);
                tvWoman.setTextColor(getResources().getColor(R.color.safe_color));
            }
        });

        findViewById(R.id.bt_singn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
    }

    private void checkInfo() {
        if (StringUtil.isEmpty(etName.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.user_name_not_null));
        } else if (StringUtil.isEmpty(etPwd.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.pwd_not_null));
        } else if (StringUtil.isEmpty(etAge.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.age_not_null));
        } else {

        }
    }
}
