package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/3/15.
 */
public class DateWheelPicker extends LinearLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
    private WheelPicker wpYear;
    private WheelPicker wpMonth;
    private WheelPicker wpDay;

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

    }
}
