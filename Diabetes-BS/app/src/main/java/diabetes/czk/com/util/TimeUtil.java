package diabetes.czk.com.util;

import java.util.Calendar;

/**
 * Created by 陈忠凯 on 2017/3/8.
 */
public class TimeUtil {
    /**
     *
     * @param timeMillis
     * @return
     */
    public static int getHourOfTheDay(long timeMillis) {
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(timeMillis);
        return mCalendar.get(Calendar.HOUR);
    }
}
