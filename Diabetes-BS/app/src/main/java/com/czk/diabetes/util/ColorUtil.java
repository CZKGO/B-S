package com.czk.diabetes.util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/5/3.
 */

public class ColorUtil {
    public static void setTitleColor(Activity ac) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ac.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ac.getResources().getColor(R.color.theme_color));
        }
    }
}
