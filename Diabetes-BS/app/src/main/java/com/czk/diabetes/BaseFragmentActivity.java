package com.czk.diabetes;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class BaseFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String language = settings.getString("language", "DEFAULT");
        if (language.equals("DEFAULT")) {
            switchLanguage(Locale.getDefault());
        } else {
            changeLanguage(language);
        }
    }

    public void switchLanguage(Locale locale) {
        Configuration config = getResources().getConfiguration();// 获得设置对象
        Resources resources = getResources();// 获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    protected void changeLanguage(String language) {
        if (language.equals("English")) {
            Locale locale = Locale.ENGLISH;
            saveLangusge("English");
            switchLanguage(locale);
        } else if (language.equals("中文")) {
            Locale locale = new Locale("zh");
            saveLangusge("中文");
            switchLanguage(locale);
        }
    }

    private void saveLangusge(String language) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("language", language);
        editor.commit();
    }
}
