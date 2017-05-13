package com.czk.diabetes.medicine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseFragmentActivity;
import com.czk.diabetes.DB.DBOpenHelper;
import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.util.ToastUtil;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/5/9.
 */

public class AddMedicineActivity extends BaseFragmentActivity {
    public final static String INTENT_ADD_TIME = "add_time";
    private String addTimeIntent;

    private static final int HANDLER_QUERY_FINSH = 0;

    //检查是否输入
    private List<MedicineRecord> records = new ArrayList<>();
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
    private List<View> viewTimeCard;
    private List<TextView> viewPeriod;
    private int period = -1;
    private CheckBox cbToggle;
    private View layoutDIYPeriod;
    private TextView tvUnit;
    private String[] times = new String[]{null, null, null, null, null, null};
    private String peroidStart;
    private String peroidEnd;
    private EditText etRemarks;
    private TextView tvStartTime;
    private TextView tvEndTime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_QUERY_FINSH:
                    setData();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        initData();
        initView();
        initAsncData();
        dealEvent();
    }

    private void initData() {
        addTimeIntent = getIntent().getStringExtra(INTENT_ADD_TIME);
    }

    private void initAsncData() {
        if (null != addTimeIntent) {
            getData();
        }
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                records.clear();
                DBOpenHelper helper = new DBOpenHelper(AddMedicineActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("medicine_record"
                        , new String[]{"add_time", "name", "doses", "time", "peroid_start", "peroid_end", "notifition", "description"}
                        , "add_time = ?"
                        , new String[]{addTimeIntent}
                        , null
                        , null
                        , "time" + " ASC");
                while (c.moveToNext()) {
                    MedicineRecord data = new MedicineRecord(
                            c.getString(c.getColumnIndex("add_time")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("doses")),
                            c.getString(c.getColumnIndex("time")),
                            c.getString(c.getColumnIndex("peroid_start")),
                            c.getString(c.getColumnIndex("peroid_end")),
                            c.getString(c.getColumnIndex("notifition")),
                            c.getString(c.getColumnIndex("description")));
                    records.add(data);
                }
                if (records.size() <= 0) {
                    finish();
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.data_eorr));
                }
                handler.sendEmptyMessage(HANDLER_QUERY_FINSH);
                c.close();
                db.close();
            }
        }).start();
    }

    private void setData() {
        etName.setText(records.get(0).name);
        etDoses.setText(records.get(0).doses);
        for (MedicineRecord record : records) {

        }
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
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        cbToggle = (CheckBox) findViewById(R.id.cb_toggle);
        setCheckBox(cbToggle, true);

        etName = (EditText) findViewById(R.id.et_name);
        etDoses = (EditText) findViewById(R.id.et_doses);
        layoutAddTime = findViewById(R.id.add_time);

        viewTimeCard = new ArrayList<>();
        viewTimeCard.add(findViewById(R.id.time_card_one));
        viewTimeCard.add(findViewById(R.id.time_card_tow));
        viewTimeCard.add(findViewById(R.id.time_card_three));
        viewTimeCard.add(findViewById(R.id.time_card_four));
        viewTimeCard.add(findViewById(R.id.time_card_five));
        viewTimeCard.add(findViewById(R.id.time_card_six));

        viewPeriod = new ArrayList<>();
        viewPeriod.add((TextView) findViewById(R.id.tv_period_1));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_2));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_3));
        viewPeriod.add((TextView) findViewById(R.id.tv_period_4));

        layoutDIYPeriod = findViewById(R.id.diy_period);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);

        etRemarks = (EditText) findViewById(R.id.et_remarks);
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
        //头部
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEvent(CHECKTIME)) {
                    for (int i = 0; i < 6; i++) {
                        if (!showTimeCard[i]) {
                            showTimeCard[i] = true;
                            String time = "8:00";

                            showTimeCard(viewTimeCard.get(i), i, time);
                            if (i >= 5) {
                                v.setVisibility(View.GONE);
                            }
                        }
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
                if (checkEvent(CHECKSAVE)) {
                    DBOpenHelper helper = new DBOpenHelper(AddMedicineActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();  //得到的是SQLiteDatabase对象
                    long time = System.currentTimeMillis();
                    for (String t : times) {
                        if (null != t) {
                            StringBuffer sBuffer = new StringBuffer();
                            sBuffer.append("REPLACE INTO medicine_record ");
                            sBuffer.append("(add_time,name,doses,time,peroid_start,peroid_end,notifition,description) values (");
                            sBuffer.append("'" + time + "',");
                            sBuffer.append("'" + etName.getText() + "',");
                            sBuffer.append("'" + etDoses.getText() + "',");
                            sBuffer.append("'" + t + "',");
                            sBuffer.append("'" + peroidStart + "',");
                            sBuffer.append("'" + peroidEnd + "',");
                            sBuffer.append("'" + String.valueOf(cbToggle.isChecked()) + "',");
                            sBuffer.append("'" + etRemarks.getText() + "'");
                            sBuffer.append(")");
                            // 执行创建表的SQL语句
                            db.execSQL(sBuffer.toString());
                        }
                    }
                    db.close();
                    finish();
                }
            }
        });

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                tvStartTime.setText(year + "-" + TimeUtil.format(month) + "-" + TimeUtil.format(day));
                            }
                        }
                        , TimeUtil.getYear(System.currentTimeMillis())
                        , TimeUtil.getMonth(System.currentTimeMillis())
                        , TimeUtil.getDay(System.currentTimeMillis())
                        , false).show(getSupportFragmentManager(), "datePicker");
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                tvEndTime.setText(year + "-" + TimeUtil.format(month) + "-" + TimeUtil.format(day));
                            }
                        }
                        , TimeUtil.getYear(System.currentTimeMillis())
                        , TimeUtil.getMonth(System.currentTimeMillis())
                        , TimeUtil.getDay(System.currentTimeMillis())
                        , false).show(getSupportFragmentManager(), "datePicker");
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

    private void showTimeCard(final View view, final int cardNumber, String time) {
        view.setVisibility(View.VISIBLE);
        ImageView ivBin = (ImageView) view.findViewById(R.id.iv_bin);
        FontIconDrawable iconVin = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_bin);
        ivBin.setImageDrawable(iconVin);

        TextView tvDoses = (TextView) view.findViewById(R.id.tv_doses);
        tvDoses.setText(etDoses.getText().toString() + tvUnit.getText());

        final TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        times[cardNumber] = time;
        tvTime.setText(time);
        
        findViewById(R.id.time_chose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                tvTime.setText(hourOfDay + ":" + minute);
                            }
                        }
                        , TimeUtil.getHourOfTheDay(System.currentTimeMillis())
                        , TimeUtil.getMinuteOfTheHour(System.currentTimeMillis())
                        , false).show(getSupportFragmentManager(), "timePicker");
            }
        });

        ivBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                layoutAddTime.setVisibility(View.VISIBLE);
                showTimeCard[cardNumber] = false;
                times[cardNumber] = null;
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
                        } else {
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_line_them_r_2));
                        }
                        view.setTextColor(ThemeUtil.getThemeColor());
                        period = viewPeriod.indexOf(view);
                        if (period == 3) {//自定义
                            tvStartTime.setText(TimeUtil.getYearMonthDay(System.currentTimeMillis()));
                            tvEndTime.setText(TimeUtil.getYearMonthDay(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
                            layoutDIYPeriod.setVisibility(View.VISIBLE);
                        } else {
                            layoutDIYPeriod.setVisibility(View.GONE);
                        }
                        peroidStart = TimeUtil.getYearMonthDay(System.currentTimeMillis());
                        switch (period) {
                            case 0://一周
                                peroidEnd = TimeUtil.getYearMonthDay(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
                                break;
                            case 1://二周
                                peroidEnd = TimeUtil.getYearMonthDay(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000);
                                break;
                            case 2://一月
                                peroidEnd = TimeUtil.getYearMonthDay(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000);
                                break;
                            case 3://自定义
                                peroidStart = tvStartTime.getText().toString();
                                peroidEnd = tvEndTime.getText().toString();
                                break;
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
            } else {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_line_them_light_r_2));
            }
            view.setTextColor(ThemeUtil.getThemeColorLight());

        }
    }
}
