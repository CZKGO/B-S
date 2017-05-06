package com.czk.diabetes.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseFragmentActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.view.SpinerPopWindow;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/4/8.
 */

public class AddNotificationActivity extends BaseFragmentActivity {
    private static long HOUR = 60 * 60 * 1000;
    private ImageView ivIcon;

    private SpinerPopWindow spwDate;
    private List<String> dateChose;
    private TextView tvDateChose;
    private TextView tvTimeChose;
    private long currentTime;
    private View vWeekList;
    private NotificationItem notificationItem;
    private int mMinute;
    private int mHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notifiction);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        dateChose = Arrays.asList(getResources().getStringArray(R.array.date_chose));
        currentTime = System.currentTimeMillis();
        notificationItem = new NotificationItem();
        mMinute = TimeUtil.getMinuteOfTheHour(currentTime);
        mHour = TimeUtil.getHourOfTheDay(currentTime);
    }

    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.add_notification));
        /**添加提醒**/
        tvDateChose = (TextView) findViewById(R.id.tv_date_chose);
        vWeekList = findViewById(R.id.date_chosed);
        ImageView ivDateTriangleDown = (ImageView) findViewById(R.id.date_triangle_down);
        FontIconDrawable fontTriangleDown = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_triangle_down);
        ivDateTriangleDown.setImageDrawable(fontTriangleDown);
        //初始化日期选择PopWindow
        spwDate = new SpinerPopWindow(AddNotificationActivity.this, dateChose);
        tvTimeChose = (TextView) findViewById(R.id.tv_time_chose);
        tvTimeChose.setText(TimeUtil.getTime(currentTime + HOUR));//因为是设置提醒，所以默认时间向后一小时
        ImageView ivTimeIcon = (ImageView) findViewById(R.id.time_icon);
        FontIconDrawable fontTimeIcon = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_clock);
        ivTimeIcon.setImageDrawable(fontTimeIcon);
    }

    private void dealEvent() {
        /**头部**/
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /**添加提醒**/
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        tvDateChose.setText(year + "-" + TimeUtil.format(month) + "-" + TimeUtil.format(day));
                    }
                }
                , TimeUtil.getYear(currentTime)
                , TimeUtil.getMonth(currentTime)
                , TimeUtil.getDay(currentTime)
                , false);
        findViewById(R.id.date_chose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spwDate.setWidth(v.getMeasuredWidth());
                spwDate.showAsDropDown(v);
            }
        });
        spwDate.setItemListener(new SpinerPopWindow.SpinerAdapter.OnItemSelectListener() {
            @Override
            public void onItemClick(int pos, String item) {
                vWeekList.setVisibility(View.GONE);
                if (item.equals(getResources().getString(R.string.diy))) {
                    datePickerDialog.show(getSupportFragmentManager(), "datePicker");
                    notificationItem.setType(NotificationItem.TYPE_DIY_DAY);
                } else {
                    tvDateChose.setText(item);
                    if (item.equals(getResources().getString(R.string.every_week))) {
                        notificationItem.setType(NotificationItem.TYPE_WEEK);
                        vWeekList.setVisibility(View.VISIBLE);
                    } else {
                        notificationItem.setType(NotificationItem.TYPE_EVERY_DAY);
                    }
                }
            }
        });

        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        tvTimeChose.setText(TimeUtil.format(hourOfDay) + ":" + TimeUtil.format(minute));
                    }
                }
                , TimeUtil.getHourOfTheDay(currentTime + HOUR)
                , TimeUtil.getMinuteOfTheHour(currentTime + HOUR), true, false);
        findViewById(R.id.time_chose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getSupportFragmentManager(), "timePicker");
            }
        });

        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationItem.setTitle(((EditText) findViewById(R.id.et_event)).getText().toString());
                notificationItem.setDescription(((EditText) findViewById(R.id.et_event)).getText().toString());
                notificationItem.setTime(mMinute,mHour);
                MYNotificationManager.getInstance().setNotifition(notificationItem, AddNotificationActivity.this);
            }
        });
    }
}
