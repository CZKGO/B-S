package com.czk.diabetes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.signin.SignActivity;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class MainFragment extends Fragment {

    private View fragment;
    private ImageView userIV;
    private TextView circularOneTile;
    private TextView circularTowTile;
    private long currentTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_mainpage, container, false);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        dealEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        //输入血糖
        currentTime = System.currentTimeMillis();
        setCircularTile(circularOneTile, circularTowTile, TimeUtil.getHourOfTheDay(System.currentTimeMillis()));
    }

    private void setCircularTile(TextView oneTile, TextView towTile, int hourOfTheDay) {
        if (0 < hourOfTheDay && hourOfTheDay <= 6) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_dawn));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.before_breakfast));
        } else if (6 < hourOfTheDay && hourOfTheDay <= 8) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_breakfast));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.after_breakfast));
        } else if (8 < hourOfTheDay && hourOfTheDay <= 11) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_breakfast));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.before_lunch));
        } else if (11 < hourOfTheDay && hourOfTheDay <= 15) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_lunch));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.after_lunch));
        } else if (15 < hourOfTheDay && hourOfTheDay <= 17) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_lunch));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.before_dinner));
        } else if (17 < hourOfTheDay && hourOfTheDay <= 22) {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.before_dinner));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.after_dinner));
        } else {
            oneTile.setText(getResources().getString(com.czk.diabetes.R.string.after_dinner));
            towTile.setText(getResources().getString(com.czk.diabetes.R.string.before_sleep));
        }
    }

    private void initView() {
        //登录卡片
        userIV = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.user_icon);
        FontIconDrawable userIVfontDrawable = FontIconDrawable.inflate(getActivity(), com.czk.diabetes.R.xml.icon_user);
        userIVfontDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.theme_color_light));
        userIV.setImageDrawable(userIVfontDrawable);

        ImageView chevronRightIV = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.img_sig_chevron_right);
        FontIconDrawable chevronRightDrawable = FontIconDrawable.inflate(getActivity(), com.czk.diabetes.R.xml.icon_chevron_thin_right);
        chevronRightDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.txt_light_color));
        chevronRightIV.setImageDrawable(chevronRightDrawable);
        //每日一读
        ImageView cozyTipIcon = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.cozy_tip_icon);
        FontIconDrawable cozyTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), com.czk.diabetes.R.xml.icon_heart);
        cozyTipIconfontDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.background_white));
        cozyTipIcon.setImageDrawable(cozyTipIconfontDrawable);
        ImageView readTipIcon = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.read_tip_icon);
        FontIconDrawable readTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), com.czk.diabetes.R.xml.icon_book);
        readTipIconfontDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.background_white));
        readTipIcon.setImageDrawable(readTipIconfontDrawable);


        //输入血糖
        circularOneTile = (TextView) fragment.findViewById(com.czk.diabetes.R.id.circular_one_tile);
        circularTowTile = (TextView) fragment.findViewById(com.czk.diabetes.R.id.circular_tow_tile);
        ImageView bloodTipIcon = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.blood_tip_icon);
        FontIconDrawable bloodTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), com.czk.diabetes.R.xml.icon_droplet);
        bloodTipIconfontDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.background_white));
        bloodTipIcon.setImageDrawable(bloodTipIconfontDrawable);
        ImageView oneIcon = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.circular_one_icon);
        bloodTipIconfontDrawable.setTextColor(getResources().getColor(com.czk.diabetes.R.color.background_white_light));
        oneIcon.setImageDrawable(bloodTipIconfontDrawable);
        ImageView towIcon = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.circular_tow_icon);
        towIcon.setImageDrawable(bloodTipIconfontDrawable);
        chevronRightIV = (ImageView) fragment.findViewById(com.czk.diabetes.R.id.img_blood_chevron_right);
        chevronRightIV.setImageDrawable(chevronRightDrawable);
    }

    private void dealEvent() {
        //登录卡片
        View signCard = fragment.findViewById(com.czk.diabetes.R.id.sign_card);
        signCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SignActivity.class));
            }
        });
        //每日一读


        //输入血糖
        View bloodSugarCard = fragment.findViewById(com.czk.diabetes.R.id.blood_sugar_card);
        bloodSugarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(), AddValueActivity.class);
                intent.putExtra("currentTime",currentTime);
                startActivity(intent);
            }
        });
    }
}
