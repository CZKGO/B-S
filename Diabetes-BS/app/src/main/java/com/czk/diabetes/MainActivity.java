package com.czk.diabetes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.czk.diabetes.aboutUs.AboutUsActvity;
import com.czk.diabetes.notification.AddNotificationActivity;
import com.czk.diabetes.setting.SettingActvity;
import com.czk.diabetes.util.FontIconDrawable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseFragmentActivity {

    private final static int START_SETTING = 0;
    private DrawerLayout drawerLayout;
    private ImageView ivIcon;
    private LinearLayout settiongLayout;
    private LinearLayout aboutLayout;
    private ViewPager mPager;
    private MainPagerAdapter adapter;
    private List<Fragment> fragments;
    private ImageView icon_bottom1;
    private ImageView icon_bottom2;
    private ImageView icon_bottom3;
    private ImageView icon_bottom4;
    private FontIconDrawable fontDrawable1;
    private FontIconDrawable fontDrawable2;
    private FontIconDrawable fontDrawable3;
    private FontIconDrawable fontDrawable4;
    private View tab1;
    private View tab2;
    private View tab3;
    private View tab4;
    private ImageView ivIconRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        setTabSelection(0);
        dealEvent();
    }

    private void initData() {
        fragments = new ArrayList<>();
        MainFragment mainFragment = new MainFragment();
        MedicineFragment medicineFragment = new MedicineFragment();
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        ContactFragment contactFragment = new ContactFragment();
        fragments.add(mainFragment);
        fragments.add(statisticsFragment);
        fragments.add(medicineFragment);
        fragments.add(contactFragment);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_menu));
        ivIconRight = (ImageView) findViewById(R.id.icon_right);
        ivIconRight.setVisibility(View.VISIBLE);
        ivIconRight.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_alarm));
        //底部
        icon_bottom1 = (ImageView) findViewById(R.id.icon_bottom1);
        fontDrawable1 = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_home);
        icon_bottom1.setImageDrawable(fontDrawable1);
        icon_bottom2 = (ImageView) findViewById(R.id.icon_bottom2);
        fontDrawable2 = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_clipboard_edit);
        icon_bottom2.setImageDrawable(fontDrawable2);
        icon_bottom3 = (ImageView) findViewById(R.id.icon_bottom3);
        fontDrawable3 = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_pill);
        icon_bottom3.setImageDrawable(fontDrawable3);
        icon_bottom4 = (ImageView) findViewById(R.id.icon_bottom4);
        fontDrawable4 = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_bubbles);
        icon_bottom4.setImageDrawable(fontDrawable4);
        tab1 = findViewById(R.id.page1);
        tab2 = findViewById(R.id.page2);
        tab3 = findViewById(R.id.page3);
        tab4 = findViewById(R.id.page4);
        //侧边栏
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView settiong_icon = (ImageView) findViewById(R.id.settiong_icon);
        settiong_icon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_cog));
        ImageView about_icon = (ImageView) findViewById(R.id.about_icon);
        about_icon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_notification));
        settiongLayout = (LinearLayout) findViewById(R.id.settiong_layout);
        aboutLayout = (LinearLayout) findViewById(R.id.about_layout);

        //主体
        mPager = (ViewPager) findViewById(R.id.main_viewpager);
        mPager.setAdapter(adapter);

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        ivIconRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNotificationActivity.class));
            }
        });

        settiongLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SettingActvity.class), START_SETTING);
            }
        });

        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AboutUsActvity.class), START_SETTING);
            }
        });

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(0);
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(1);
            }
        });

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(2);
            }
        });

        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(3);
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        fontDrawable1.setTextColor(getResources().getColor(R.color.theme_color_light));
        icon_bottom1.setImageDrawable(fontDrawable1);
        fontDrawable2.setTextColor(getResources().getColor(R.color.theme_color_light));
        icon_bottom2.setImageDrawable(fontDrawable2);
        fontDrawable3.setTextColor(getResources().getColor(R.color.theme_color_light));
        icon_bottom3.setImageDrawable(fontDrawable3);
        fontDrawable4.setTextColor(getResources().getColor(R.color.theme_color_light));
        icon_bottom4.setImageDrawable(fontDrawable4);
        switch (index) {
            case 0:
                setTabChose(fontDrawable1, icon_bottom1);
                break;
            case 1:
                setTabChose(fontDrawable2, icon_bottom2);
                break;
            case 2:
                setTabChose(fontDrawable3, icon_bottom3);
                break;
            case 3:
                setTabChose(fontDrawable4, icon_bottom4);
                break;
            default:
                break;
        }

        mPager.setCurrentItem(index);
    }

    /**
     * 设置选中的tab状态
     * @param fontDrawable
     * @param imageView
     */
    private void setTabChose(FontIconDrawable fontDrawable, ImageView imageView) {
        fontDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        imageView.setImageDrawable(fontDrawable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case START_SETTING:
                recreate();
                break;
        }
    }

}
