package com.czk.diabetes.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 陈忠凯 on 2017/3/8.
 */
public class TimeUtil {

    /**
     * @param timeMillis
     * @return
     */
    public static int getHourOfTheDay(long timeMillis) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timeMillis);
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }


    public static String getYearMonthDay(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(timeMillis);
        return formatter.format(curDate);
    }

    public static int getYear(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }

    public static int getMonth(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }

    public static int getDay(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }
}
