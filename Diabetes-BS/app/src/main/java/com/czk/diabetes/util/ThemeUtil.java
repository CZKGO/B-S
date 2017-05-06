package com.czk.diabetes.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/5/3.
 */

public class ThemeUtil {
    public static final int THEME_BLUE = 0;
    public static final int THEME_PINK = 1;
    public static final int THEME_PURPLE = 2;
    public static final int THEME_BLACK = 3;

    public static void setTitleColor(Activity ac) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = ac.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getThemeColor());
        }
    }

    public static int getThemeColor() {
        int color = Color.BLACK;
        switch (getTheme()){
            case THEME_BLUE:
                color = MyApplication.getInstance().getResources().getColor(R.color.blue_color);
                break;
            case THEME_PINK:
                color = MyApplication.getInstance().getResources().getColor(R.color.pink_color);
                break;
            case THEME_PURPLE:
                color = MyApplication.getInstance().getResources().getColor(R.color.purple_color);
                break;
            case THEME_BLACK:
                color = MyApplication.getInstance().getResources().getColor(R.color.black_color);
                break;
        }
        return color;
    }

    public static int getThemeColorLight() {
        int color = Color.BLACK;
        switch (getTheme()){
            case THEME_BLUE:
                color = MyApplication.getInstance().getResources().getColor(R.color.blue_color_light);
                break;
            case THEME_PINK:
                color = MyApplication.getInstance().getResources().getColor(R.color.pink_color_light);
                break;
            case THEME_PURPLE:
                color = MyApplication.getInstance().getResources().getColor(R.color.purple_color_light);
                break;
            case THEME_BLACK:
                color = MyApplication.getInstance().getResources().getColor(R.color.black_color_light);
                break;
        }
        return color;
    }

    public static void setTheme(Context context) {
        switch (getTheme()){
            case THEME_BLUE:
                context.setTheme(R.style.AppTheme_Blue);
                break;
            case THEME_PINK:
                context.setTheme(R.style.AppTheme_Pink);
                break;
            case THEME_PURPLE:
                context.setTheme(R.style.AppTheme_Purple);
                break;
            case THEME_BLACK:
                context.setTheme(R.style.AppTheme_Black);
                break;
        }
    }

    public static void setTheme(int theme) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SharedPreferencesUtils.THEME, theme);
        editor.commit();
        setTheme(MyApplication.getInstance());
    }

    public static int getTheme() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        return sp.getInt(SharedPreferencesUtils.THEME, THEME_BLUE);
    }

    public static String getThemeName() {
        String themeName = "";
        switch (getTheme()){
            case THEME_BLUE:
                themeName = MyApplication.getInstance().getString(R.string.theme_blue);
                break;
            case THEME_PINK:
                themeName = MyApplication.getInstance().getString(R.string.theme_pink);
                break;
            case THEME_PURPLE:
                themeName = MyApplication.getInstance().getString(R.string.theme_purple);
                break;
            case THEME_BLACK:
                themeName = MyApplication.getInstance().getString(R.string.theme_black);
                break;
        }
        return themeName;
    }
}
