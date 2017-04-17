package com.czk.diabetes.notification;

import android.os.Bundle;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.view.SpinerPopWindow;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/4/8.
 */

public class AddNotificationActivity extends BaseActivity {
    private SpinerPopWindow spwDate;
    private List<String> dateChose;

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
    }

    private void initView() {
        //初始化PopWindow
        spwDate = new SpinerPopWindow(AddNotificationActivity.this);
//        SpinerPopWindow.SpinerAdapter mSpinerAdapter = new SpinerPopWindow.SpinerAdapter();
//        spwDate.setAdatper(mSpinerAdapter);
    }

    private void dealEvent() {
        spwDate.setItemListener();
    }
}
