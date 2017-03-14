package com.czk.diabetes;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.view.Digitalkeyboard;
import com.czk.diabetes.view.MeterView;
import com.czk.diabetes.view.WheelPicker;

import java.lang.reflect.Method;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class AddValueActivity extends BaseActivity {
    private ImageView ivIcon;
    private ImageView ivIconAdd;
    private EditText etValue;
    private Digitalkeyboard digitalkeyboard;
    private long currentTime;
    private MeterView meterView;
    private TextView tvDate;
    private WheelPicker wheelpicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        currentTime = getIntent().getLongExtra("currentTime", System.currentTimeMillis());
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), com.czk.diabetes.R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(com.czk.diabetes.R.id.title);
        tvTitle.setText(getResources().getString(com.czk.diabetes.R.string.input_measured_value));
        //主体
        meterView = (MeterView) findViewById(R.id.meter);
        tvDate = (TextView) findViewById(com.czk.diabetes.R.id.tv_date);
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
        digitalkeyboard = (Digitalkeyboard) findViewById(R.id.digitalkeyboard);
        wheelpicker = (WheelPicker) findViewById(R.id.wheelpicker);

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //主体
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == digitalkeyboard.getVisibility()) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            digitalkeyboard,
                            "rotationY",
                            0,
                            180);
                    animator.setDuration(500);
                    animator.start();

                }
            }
        });
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty())
                    meterView.setValue(Float.valueOf(s.toString()));
                else
                    meterView.setValue(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //键盘
        digitalkeyboard.setKeyClickListener(new Digitalkeyboard.OnKeyClickListener() {
            @Override
            public void onClick(int key) {
                String value = etValue.getText().toString();
                if (-1 == key) {
                    if (value.length() > 0)
                        value = value.substring(0, value.length() - 1);
                } else if (-2 == key) {
                    if (value.length() < 2 && value.length() > 0)
                        value = value + ".";
                } else if (value.length() < 2 || (value.contains(".") && value.length() < 3)) {
                    value = value + key;
                }
                if (value.length() > 0) {
                    if (Float.parseFloat(value) <= 25) {
                        etValue.setText(value);
                        etValue.setSelection(value.length());
                    }
                } else {
                    etValue.setText(value);
                    //移动光标
                    etValue.setSelection(value.length());
                }
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
