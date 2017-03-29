package com.czk.diabetes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.czk.diabetes.medicine.SearchMedicineActivity;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class MedicineFragment extends Fragment {
    private View fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_medicine, container, false);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {

    }

    private void initView() {
        /**************************************************
         * 药品                                           *
         **************************************************/
        ImageView leechdomIconIV = (ImageView) fragment.findViewById(R.id.leechdom_icon);
        FontIconDrawable leechdomIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_pill);
        leechdomIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        leechdomIconIV.setImageDrawable(leechdomIconDrawable);
        ImageView leechdomLeftIconIV = (ImageView) fragment.findViewById(R.id.leechdom_left_icon);
        FontIconDrawable leechdomIconLeftDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_pill);
        leechdomIconLeftDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        leechdomLeftIconIV.setImageDrawable(leechdomIconLeftDrawable);

        ImageView leechdomRightIconIV = (ImageView) fragment.findViewById(R.id.leechdom_right_icon);
        FontIconDrawable leechdomRightIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_injection);
        leechdomRightIconDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        leechdomRightIconIV.setImageDrawable(leechdomRightIconDrawable);
        /**************************************************
         * 饮食                                           *
         **************************************************/
        ImageView dietIconIV = (ImageView) fragment.findViewById(R.id.diet_icon);
        FontIconDrawable dietIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_spoon_knife);
        dietIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        dietIconIV.setImageDrawable(dietIconDrawable);
        ImageView dietLeftIconIV = (ImageView) fragment.findViewById(R.id.diet_left_icon);
        FontIconDrawable dietIconLeftDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_spoon_knife);
        dietIconLeftDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        dietLeftIconIV.setImageDrawable(dietIconLeftDrawable);

        ImageView dietRightIconIV = (ImageView) fragment.findViewById(R.id.diet_right_icon);
        FontIconDrawable dietRightIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_room_service);
        dietRightIconDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        dietRightIconIV.setImageDrawable(dietRightIconDrawable);
        /**************************************************
         * 运动                                           *
         **************************************************/
        ImageView athleticsIconIV = (ImageView) fragment.findViewById(R.id.athletics_icon);
        FontIconDrawable athleticsIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_directions_run);
        athleticsIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        athleticsIconIV.setImageDrawable(athleticsIconDrawable);
        ImageView athleticsLeftIconIV = (ImageView) fragment.findViewById(R.id.athletics_left_icon);
        FontIconDrawable athleticsLeftIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_directions_run);
        athleticsLeftIconDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        athleticsLeftIconIV.setImageDrawable(athleticsLeftIconDrawable);

        ImageView athleticsRightIconIV = (ImageView) fragment.findViewById(R.id.athletics_right_icon);
        FontIconDrawable athleticsRightIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_pool);
        athleticsRightIconDrawable.setTextColor(getResources().getColor(R.color.theme_color));
        athleticsRightIconIV.setImageDrawable(athleticsRightIconDrawable);
    }

    private void dealEvent() {
        fragment.findViewById(R.id.card_leechdom_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchMedicineActivity.class);
                startActivity(intent);
            }
        });
    }

}
