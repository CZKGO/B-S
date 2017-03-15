package com.czk.diabetes;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.view.Digitalkeyboard;
import com.czk.diabetes.view.MeterView;
import com.czk.diabetes.view.WheelPicker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
    private KeyBoardType keyBoardType;
    private View viewValue;
    private TextView tvTimeSlot;
    private List<String> timeSlots;
    private WheelPicker dateWheelpicker;

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
        keyBoardType = KeyBoardType.DIGITAL_TYPE;
        timeSlots = Arrays.asList(getResources().getStringArray(R.array.time_slots));
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.input_measured_value));
        //主体
        meterView = (MeterView) findViewById(R.id.meter);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvDate.setText(TimeUtil.getYearMonthDay(currentTime));
        tvTimeSlot = (TextView) findViewById(R.id.tv_time_slot);
        setCircularTile(tvTimeSlot, TimeUtil.getHourOfTheDay(currentTime));
        etValue = (EditText) findViewById(R.id.et_value);
        viewValue = findViewById(R.id.value_range);

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
        dateWheelpicker = (WheelPicker) findViewById(R.id.date_wheelpicker);

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

            }
        });
        tvTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyBoardType == KeyBoardType.DIGITAL_TYPE) {
                    wheelpicker.setData(timeSlots);
                    keyBoardType = keyBoardType.DATE_TYPE;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            digitalkeyboard,
                            "rotationY",
                            0,
                            90);
                    animator.setDuration(300);
                    animator.start();
                    final ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                            wheelpicker,
                            "rotationY",
                            -90,
                            0);
                    animator2.setDuration(300);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            digitalkeyboard.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animator2.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            wheelpicker.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    tvTimeSlot.setTextColor(getResources().getColor(R.color.theme_color));
                    etValue.setEnabled(false);
                }
            }
        });

        viewValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyBoardType == KeyBoardType.DATE_TYPE) {
                    keyBoardType = keyBoardType.DIGITAL_TYPE;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            wheelpicker,
                            "rotationY",
                            0,
                            90);
                    animator.setDuration(300);
                    animator.start();
                    final ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                            digitalkeyboard,
                            "rotationY",
                            -90,
                            0);
                    animator2.setDuration(300);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            wheelpicker.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animator2.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            digitalkeyboard.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    tvTimeSlot.setTextColor(getResources().getColor(R.color.txt_light_color));
                    etValue.setEnabled(true);
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

        wheelpicker.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {

            }

            @Override
            public void onWheelSelected(int position) {
                tvTimeSlot.setText(timeSlots.get(position));
            }

            @Override
            public void onWheelScrollStateChanged(int state) {

            }
        });
    }


    private void setCircularTile(TextView oneTile, int hourOfTheDay) {
        if (0 < hourOfTheDay && hourOfTheDay <= 6) {
            oneTile.setText(getResources().getString(R.string.before_dawn));
        } else if (6 < hourOfTheDay && hourOfTheDay <= 8) {
            oneTile.setText(getResources().getString(R.string.before_breakfast));
        } else if (8 < hourOfTheDay && hourOfTheDay <= 11) {
            oneTile.setText(getResources().getString(R.string.after_breakfast));
        } else if (11 < hourOfTheDay && hourOfTheDay <= 15) {
            oneTile.setText(getResources().getString(R.string.before_lunch));
        } else if (15 < hourOfTheDay && hourOfTheDay <= 17) {
            oneTile.setText(getResources().getString(R.string.after_lunch));
        } else if (17 < hourOfTheDay && hourOfTheDay <= 22) {
            oneTile.setText(getResources().getString(R.string.before_dinner));
        } else {
            oneTile.setText(getResources().getString(R.string.after_dinner));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private enum KeyBoardType {
        DIGITAL_TYPE,DATE_TYPE,TIME_TYPE
    }
}
