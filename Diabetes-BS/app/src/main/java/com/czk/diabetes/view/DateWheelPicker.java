package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.czk.diabetes.R;
import com.czk.diabetes.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/3/15.
 */
public class DateWheelPicker extends LinearLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
    private WheelPicker wpYear;
    private WheelPicker wpMonth;
    private WheelPicker wpDay;
    private List<Integer> years;
    private List<Integer> months;
    private List<Integer> days;
    private OnItemSelectedListener onItemSelectedListener;

    public DateWheelPicker(Context context) {
        super(context);
        initView();
    }

    public DateWheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DateWheelPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateWheelPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_wheelpicker_date, this);

        wpYear = (WheelPicker) findViewById(R.id.wheelpicker_year);
        wpMonth = (WheelPicker) findViewById(R.id.wheelpicker_month);
        wpDay = (WheelPicker) findViewById(R.id.wheelpicker_day);

        wpYear.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                if(onItemSelectedListener!=null){
                    onItemSelectedListener.onItemSelected(data.toString()
                            +"-"+wpMonth.getCurrentItem().toString()
                            +"-"+wpDay.getCurrentItem().toString());
                }
            }
        });

        wpMonth.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                if(onItemSelectedListener!=null){
                    onItemSelectedListener.onItemSelected(wpYear.getCurrentItem().toString()
                            +"-"+data.toString()
                            +"-"+wpDay.getCurrentItem().toString());
                }
            }
        });

        wpDay.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                if(onItemSelectedListener!=null){
                    onItemSelectedListener.onItemSelected(wpYear.getCurrentItem().toString()
                            +"-"+wpMonth.getCurrentItem().toString()
                            +"-"+data.toString());
                }
            }
        });

        years = new ArrayList<>();
        int year =TimeUtil.getYear(System.currentTimeMillis());
        for(int i = 0;i<19;i++){
            years.add(year-i);
        }
        wpYear.setData(years);

        months = new ArrayList<>();
        for(int i = 1;i<13;i++){
            months.add(i);
        }
        wpMonth.setData(months);

        days = new ArrayList<>();
        for(int i = 1;i<32;i++){
            days.add(i);
        }
        wpDay.setData(days);
    }

    public void setSelectedYear(int selectedYear) {
        wpYear.setSelectedItemPosition(years.indexOf(selectedYear));
    }

    public void setSelectedMonth(int selectedMonth) {
        wpMonth.setSelectedItemPosition(months.indexOf(selectedMonth));
    }

    public void setSelectedDay(int selectedDay) {
        wpDay.setSelectedItemPosition(days.indexOf(selectedDay));
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener{
        void onItemSelected(String data);
    }
}
