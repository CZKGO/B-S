package com.czk.diabetes;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class AddValueActivity extends BaseActivity {
    private ImageView ivIcon;
    private ImageView ivIconAdd;
    private EditText etValue;
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
    private long currentTime;
    private TextView tvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.czk.diabetes.R.layout.activity_add_value);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        currentTime = getIntent().getLongExtra("currentTime", System.currentTimeMillis());
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(com.czk.diabetes.R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), com.czk.diabetes.R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(com.czk.diabetes.R.id.title);
        tvTitle.setText(getResources().getString(com.czk.diabetes.R.string.input_measured_value));
        //主体
        tvValue = (TextView) findViewById(com.czk.diabetes.R.id.add_value);
        TextView tvDate = (TextView) findViewById(com.czk.diabetes.R.id.tv_date);
        tvDate.setText(TimeUtil.getYearMonthDay(currentTime));
        TextView tvTimeSlot = (TextView) findViewById(com.czk.diabetes.R.id.tv_time_slot);
        setCircularTile(tvTimeSlot, TimeUtil.getHourOfTheDay(currentTime));
        etValue = (EditText) findViewById(com.czk.diabetes.R.id.et_value);
         /*设置EditText光标可见但不弹出软键盘*/
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            etValue.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(etValue, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //键盘
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

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //主体
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvValue.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //键盘
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
                        String value = etValue.getText().toString();
                        if (-1 == i) {
                            if (value.length() > 0)
                                value = value.substring(0, value.length() - 1);
                        } else if (-2 == i) {
                            if (value.length() < 2 && value.length() > 0)
                                value = value + ".";
                        } else if (value.length() < 2 || (value.contains(".") && value.length() < 3)) {
                            value = value + i;
                        }
                        if (value.length() > 0) {
                            if (Float.parseFloat(value) <= 25) {
                                etValue.setText(value);
                                etValue.setSelection(value.length());
                            }
                        } else {
                            etValue.setText(value);
                            etValue.setSelection(value.length());
                        }

                        break;
                }
                return true;
            }
        });
    }

    private void setCircularTile(TextView oneTile, int hourOfTheDay) {
        if (0 < hourOfTheDay && hourOfTheDay <= 6) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_dawn));
        } else if (6 < hourOfTheDay && hourOfTheDay <= 8) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_breakfast));
        } else if (8 < hourOfTheDay && hourOfTheDay <= 11) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_breakfast));
        } else if (11 < hourOfTheDay && hourOfTheDay <= 15) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_lunch));
        } else if (15 < hourOfTheDay && hourOfTheDay <= 17) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_lunch));
        } else if (17 < hourOfTheDay && hourOfTheDay <= 22) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_dinner));
        } else {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_dinner));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
