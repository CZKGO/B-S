package diabetes.czk.com.util;

import android.util.Log;

/**
 * Created by 陈忠凯 on 2017/2/21.
 */
public class LogUtil {

    private static boolean LogEnabled = true;

    public static boolean isLogEnabled() {
        return LogEnabled;
    }

    public static void d(String TAG, String msg) {
        if (LogEnabled)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (LogEnabled)
            Log.e(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (LogEnabled)
            Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (LogEnabled)
            Log.v(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (LogEnabled)
            Log.w(TAG, msg);
    }

}
