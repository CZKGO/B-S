package com.czk.diabetes.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.R;
import com.czk.diabetes.util.ColorUtil;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/4.
 */
public class SettingActvity extends PreferenceActivity {
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtil.setTitleColor(this);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.setting_perference);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        Preference languagePre = findPreference("language");
//        languagePre.setSummary();
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.setting));

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
