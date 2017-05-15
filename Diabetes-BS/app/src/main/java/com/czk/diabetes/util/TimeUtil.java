package com.czk.diabetes.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 陈忠凯 on 2017/3/8.
 */
public class TimeUtil {

    public static final long ONE_WEEK = 604800000l;//7 * 24 * 60 * 60 * 1000;
    public static final long ONE_MONTH = 2592000000l;//30 * 24 * 60 * 60 * 1000;

    /**
     * 得到给定时间的时钟
     *
     * @param timeMillis
     * @return
     */
    public static int getHourOfTheDay(long timeMillis) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timeMillis);
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 得到给定时间的分钟
     *
     * @param timeMillis
     * @return
     */
    public static int getMinuteOfTheHour(long timeMillis) {
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(timeMillis);
        return mCalendar.get(Calendar.MINUTE);
    }

    /**
     * 得到给定时间的年月日表示
     *
     * @param timeMillis
     * @return
     */
    public static String getYearMonthDay(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(timeMillis);
        return formatter.format(curDate);
    }

    /**
     * 得到给定时间的年份
     *
     * @param timeMillis
     * @return
     */
    public static int getYear(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }

    /**
     * 得到给定时间的月份
     *
     * @param timeMillis
     * @return
     */
    public static int getMonth(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }

    /**
     * 得到给定时间的天（一月中的）
     *
     * @param timeMillis
     * @return
     */
    public static int getDay(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(timeMillis);
        return Integer.parseInt(formatter.format(curDate));
    }

    /**
     * 得到给定时间的时钟和分钟
     *
     * @param timeMillis
     * @return
     */
    public static String getTime(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(timeMillis);
        return formatter.format(curDate);
    }

    /**
     * 一位数前加0，eg：01
     *
     * @param hourOfDay
     * @return
     */
    public static String format(int hourOfDay) {
        String result = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            result = "0" + result;
        }
        return result;
    }


    /**
     * @param strTime    要转换的String类型的时间,strTime的时间格式和formatType的时间格式必须相同
     * @param formatType 时间格式
     * @return
     * @throws ParseException
     */
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    /**
     * @param strTime    要转换的string类型的时间
     * @param formatType 要转换的格式
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    /**
     * 要转换的date类型的时间
     *
     * @param date
     * @return
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 将时间转换成指定格式
     *
     * @param timeMillis
     * @param format
     * @return
     */
    public static String getSringByFormat(long timeMillis, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }
}
