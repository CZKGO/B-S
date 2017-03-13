package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/3/13.
 */
public class Digitalkeyboard extends LinearLayout implements View.OnClickListener {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
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
    }

    @Override
    public void onClick(View v) {

    }
}
