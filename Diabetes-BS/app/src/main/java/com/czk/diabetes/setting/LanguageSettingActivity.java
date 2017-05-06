package com.czk.diabetes.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/6.
 */
public class LanguageSettingActivity extends BaseActivity {
    private ImageView ivIcon;
    private String[] languageList;
    private LanguageSettingAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_languge);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        languageList = getResources().getStringArray(R.array.languages);
        adapter = new LanguageSettingAdapter(LanguageSettingActivity.this, languageList, 0);
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.language));

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
                changeLanguage(languageList[position]);
                Intent intent = new Intent(LanguageSettingActivity.this,
                        SettingActvity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        });
    }
}
