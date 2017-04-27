package com.czk.diabetes.util;

import android.content.Context;

/**
 * Created by 陈忠凯 on 2017/4/27.
 */

public class DimensUtil {
    /**
     * dp值转px值
     * @param context
     * @param dp
     * @return
     */
    public static int dpTopx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float getScreenWidthDip(Context context) {
        return context.getResources().getDisplayMetrics().xdpi;
    }
}
