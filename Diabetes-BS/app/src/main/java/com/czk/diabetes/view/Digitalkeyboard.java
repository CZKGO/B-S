package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/3/13.
 */
public class Digitalkeyboard extends LinearLayout implements View.OnClickListener {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
    private TextView tvNumber0;
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvNumber3;
    private TextView tvNumber4;
    private TextView tvNumber5;
    private TextView tvNumber6;
    private TextView tvNumber7;
    private TextView tvNumber8;
    private TextView tvNumber9;
    private TextView tvNumberDelete;
    private TextView tvNumberPoint;
    public Digitalkeyboard(Context context) {
        super(context);
        initView();
    }

    public Digitalkeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Digitalkeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Digitalkeyboard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }


    private void initView() {
        View.inflate(getContext(), R.layout.view_digital_keyboard, this);

        tvNumber0 = (TextView) findViewById(com.czk.diabetes.R.id.number_0);
        tvNumber1 = (TextView) findViewById(com.czk.diabetes.R.id.number_1);
        tvNumber2 = (TextView) findViewById(com.czk.diabetes.R.id.number_2);
        tvNumber3 = (TextView) findViewById(com.czk.diabetes.R.id.number_3);
        tvNumber4 = (TextView) findViewById(com.czk.diabetes.R.id.number_4);
        tvNumber5 = (TextView) findViewById(com.czk.diabetes.R.id.number_5);
        tvNumber6 = (TextView) findViewById(com.czk.diabetes.R.id.number_6);
        tvNumber7 = (TextView) findViewById(com.czk.diabetes.R.id.number_7);
        tvNumber8 = (TextView) findViewById(com.czk.diabetes.R.id.number_8);
        tvNumber9 = (TextView) findViewById(com.czk.diabetes.R.id.number_9);
        tvNumberDelete = (TextView) findViewById(com.czk.diabetes.R.id.number_delete);
        tvNumberPoint = (TextView) findViewById(com.czk.diabetes.R.id.number_point);
    }

    @Override
    public void onClick(View v) {

    }
}
