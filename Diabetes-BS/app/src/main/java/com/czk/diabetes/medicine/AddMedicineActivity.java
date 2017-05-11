package com.czk.diabetes.medicine;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/5/9.
 */

public class AddMedicineActivity extends BaseActivity {
    //检查是否输入
    final private static int CHECKNAME = 0;
    final private static int CHECKDOSES = 1;
    final private static int CHECKTIME = 2;
    final private static int CHECKPERIOD = 3;
    final private static int CHECKSAVE = 4;

    private ImageView ivIcon;
    private boolean showTimeCard[] = new boolean[]{false, false, false, false, false, false};//决定展示第几个时间卡片
    private View layoutAddTime;
    private EditText etName;
    private EditText etDoses;
    private List<TextView> viewPeriod;
    private int period = -1;
    private CheckBox cbToggle;
    private View layoutDIYPeriod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
//        initData();
        initView();
        dealEvent();
    }


    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.add_leechdom));
        /**主体**/
        ImageView ivAdd = (ImageView) findViewById(R.id.iv_add);
        FontIconDrawable iconPlus = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_plus);
        ivAdd.setImageDrawable(iconPlus);
        cbToggle = (CheckBox) findViewById(R.id.cb_toggle);
        setCheckBox(cbToggle, true);

        etName = (EditText) findViewById(R.id.et_name);
        etDoses = (EditText) findViewById(R.id.et_doses);
        layoutAddTime = findViewById(R.id.add_time);

        viewPeriod = new ArrayList<>();
        viewPeriod.add((TextView) findViewById(R.id.tv_period_1));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_2));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_3));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_4));

        layoutDIYPeriod = findViewById(R.id.diy_period);
    }

    private void setCheckBox(CheckBox checkBox, boolean check) {
        if (check) {
            FontIconDrawable iconToggle = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_toggle_on);
            checkBox.setBackgroundDrawable(iconToggle);
            checkBox.setChecked(check);
        } else {
            FontIconDrawable iconToggle = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_toggle_off);
            checkBox.setBackgroundDrawable(iconToggle);
            checkBox.setChecked(check);
        }
    }


    private void dealEvent() {
        layoutAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEvent(CHECKTIME)) {
                    if (!showTimeCard[0]) {
                        showTimeCard[0] = true;
                        showTimeCard(findViewById(R.id.time_card_one), 0);
                    } else if (!showTimeCard[1]) {
                        showTimeCard[1] = true;
                        showTimeCard(findViewById(R.id.time_card_tow), 1);
                    } else if (!showTimeCard[2]) {
                        showTimeCard[2] = true;
                        showTimeCard(findViewById(R.id.time_card_three), 2);
                    } else if (!showTimeCard[3]) {
                        showTimeCard[3] = true;
                        showTimeCard(findViewById(R.id.time_card_four), 2);
                    } else if (!showTimeCard[4]) {
                        showTimeCard[4] = true;
                        showTimeCard(findViewById(R.id.time_card_five), 4);
                    } else if (!showTimeCard[5]) {
                        showTimeCard[5] = true;
                        showTimeCard(findViewById(R.id.time_card_six), 5);
                        v.setVisibility(View.GONE);
                    }
                }
            }
        });

        setPeriodEvent(viewPeriod);

        findViewById(R.id.layout_notifition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbToggle.isChecked()) {
                    setCheckBox(cbToggle, false);
                } else {
                    setCheckBox(cbToggle, true);
                }
            }
        });

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEvent(CHECKSAVE)){

                }
            }
        });
    }

    private boolean checkEvent(int type) {
        boolean doEvent = true;
        if (type > CHECKNAME && StringUtil.isEmpty(etName.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddMedicineActivity.this, getResources().getString(R.string.please_input_drag_name));
            etName.setFocusable(true);
            etName.setFocusableInTouchMode(true);
            etName.requestFocus();
            etName.findFocus();
        } else if (type > CHECKDOSES && StringUtil.isEmpty(etDoses.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddMedicineActivity.this, getResources().getString(R.string.please_input_drag_doses));
            etDoses.setFocusable(true);
            etDoses.setFocusableInTouchMode(true);
            etDoses.requestFocus();
            etDoses.findFocus();
        } else if (type > CHECKTIME && !(showTimeCard[0] || showTimeCard[1] || showTimeCard[2] || showTimeCard[3] || showTimeCard[4] || showTimeCard[5])) {
            doEvent = false;
            ToastUtil.showShortToast(AddMedicineActivity.this, getResources().getString(R.string.please_choose_medication_time));
        } else if (type > CHECKPERIOD && -1 == period) {
            doEvent = false;
            ToastUtil.showShortToast(AddMedicineActivity.this, getResources().getString(R.string.please_choose_medication_period));
        }
        return doEvent;
    }

    private void showTimeCard(final View view, final int cardNumber) {
        view.setVisibility(View.VISIBLE);
        ImageView ivBin = (ImageView) view.findViewById(R.id.iv_bin);
        FontIconDrawable iconVin = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_bin);
        ivBin.setImageDrawable(iconVin);

        ivBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                layoutAddTime.setVisibility(View.VISIBLE);
                showTimeCard[cardNumber] = false;
            }
        });
    }

    public void setPeriodEvent(final List<TextView> viewPeriod) {
        for (final TextView view : viewPeriod) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkEvent(CHECKPERIOD)) {
                        clearPeriondStatus(viewPeriod);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.setBackground(getResources().getDrawable(R.drawable.rectangle_line_them_r_2, getTheme()));
                        }else {
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_line_them_r_2));
                        }
                        view.setTextColor(ThemeUtil.getThemeColor());
                        period = viewPeriod.indexOf(view);
                        if(period == 3){//自定义
                            layoutDIYPeriod.setVisibility(View.VISIBLE);
                        }else {
                            layoutDIYPeriod.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void clearPeriondStatus(List<TextView> viewPeriod) {
        for (TextView view : viewPeriod) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getResources().getDrawable(R.drawable.rectangle_line_them_light_r_2, getTheme()));
            }else {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_line_them_light_r_2));
            }
            view.setTextColor(ThemeUtil.getThemeColorLight());
        }
    }
}
