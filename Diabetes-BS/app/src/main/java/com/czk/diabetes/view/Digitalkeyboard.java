package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/3/13.
 */
public class Digitalkeyboard extends LinearLayout {
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
    private OnKeyClickListener keyClickListener;

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

        tvNumber0 = (TextView) findViewById(R.id.number_0);
        tvNumber1 = (TextView) findViewById(R.id.number_1);
        tvNumber2 = (TextView) findViewById(R.id.number_2);
        tvNumber3 = (TextView) findViewById(R.id.number_3);
        tvNumber4 = (TextView) findViewById(R.id.number_4);
        tvNumber5 = (TextView) findViewById(R.id.number_5);
        tvNumber6 = (TextView) findViewById(R.id.number_6);
        tvNumber7 = (TextView) findViewById(R.id.number_7);
        tvNumber8 = (TextView) findViewById(R.id.number_8);
        tvNumber9 = (TextView) findViewById(R.id.number_9);
        tvNumberDelete = (TextView) findViewById(R.id.number_delete);
        tvNumberPoint = (TextView) findViewById(R.id.number_point);

        setKeyOnClickListener(tvNumber0, 0);
        setKeyOnClickListener(tvNumber1, 1);
        setKeyOnClickListener(tvNumber2, 2);
        setKeyOnClickListener(tvNumber3, 3);
        setKeyOnClickListener(tvNumber4, 4);
        setKeyOnClickListener(tvNumber5, 5);
        setKeyOnClickListener(tvNumber6, 6);
        setKeyOnClickListener(tvNumber7, 7);
        setKeyOnClickListener(tvNumber8, 8);
        setKeyOnClickListener(tvNumber9, 9);
        setKeyOnClickListener(tvNumberDelete, -1);
        setKeyOnClickListener(tvNumberPoint, -2);
    }

    private void setKeyOnClickListener(final TextView view, final int i) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setTextColor(getResources().getColor(com.czk.diabetes.R.color.txt_white));
                        view.setBackgroundResource(com.czk.diabetes.R.drawable.button_key_onclick);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setTextColor(getResources().getColor(com.czk.diabetes.R.color.theme_color));
                        view.setBackgroundResource(com.czk.diabetes.R.drawable.button_key_default);
                        if (null != keyClickListener) {
                            keyClickListener.onClick(i);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void setKeyClickListener(OnKeyClickListener keyClickListener) {
        this.keyClickListener = keyClickListener;
    }

    public interface OnKeyClickListener {
        public void onClick(int key);
    }
}
