package com.czk.diabetes.notification;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by 陈忠凯 on 2017/4/19.
 */

public class NotificationItem {
    public static final int TYPE_EVERY_DAY = 0;
    public static final int TYPE_WEEK = 1;
    public static final int TYPE_DIY_DAY = 2;
    private int id;
    private int type;
    private String title;
    private String description;
    private long time;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime(int minute, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 选择的定时时间
        long selectTime = calendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (System.currentTimeMillis() > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        setTime(selectTime);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
