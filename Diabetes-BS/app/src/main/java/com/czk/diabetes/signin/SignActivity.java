package com.czk.diabetes.signin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class SignActivity extends BaseActivity {
    private ImageView ivIcon;
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
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.sign_in));
        //主体
        ImageView ivIconName = (ImageView) findViewById(R.id.icon_name);
        ivIconName.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_user));
        ImageView ivIconPassword = (ImageView) findViewById(R.id.icon_password);
        ivIconPassword.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_lock_rounded));


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
