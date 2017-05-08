package com.czk.diabetes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.czk.diabetes.athletic.AddAthleticActivity;
import com.czk.diabetes.medicine.AddMedicineActivity;
import com.czk.diabetes.recipe.AddRecipeActivity;
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
        leechdomLeftIconIV.setImageDrawable(leechdomIconDrawable);

        /**************************************************
         * 饮食                                           *
         **************************************************/
        ImageView dietIconIV = (ImageView) fragment.findViewById(R.id.diet_icon);
        FontIconDrawable dietIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_spoon_knife);
        dietIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        dietIconIV.setImageDrawable(dietIconDrawable);
        ImageView dietLeftIconIV = (ImageView) fragment.findViewById(R.id.diet_left_icon);
        dietLeftIconIV.setImageDrawable(dietIconDrawable);
        /**************************************************
         * 运动                                           *
         **************************************************/
        ImageView athleticsIconIV = (ImageView) fragment.findViewById(R.id.athletics_icon);
        FontIconDrawable athleticsIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_directions_run);
        athleticsIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        athleticsIconIV.setImageDrawable(athleticsIconDrawable);
        ImageView athleticsLeftIconIV = (ImageView) fragment.findViewById(R.id.athletics_left_icon);
        athleticsLeftIconIV.setImageDrawable(athleticsIconDrawable);

    }

    private void dealEvent() {
        fragment.findViewById(R.id.card_leechdom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddMedicineActivity.class);
                startActivity(intent);
            }
        });

        fragment.findViewById(R.id.card_diet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                startActivity(intent);
            }
        });


        fragment.findViewById(R.id.card_athletics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAthleticActivity.class);
                startActivity(intent);
            }
        });

//        fragment.findViewById(R.id.card_leechdom_query).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchMedicineActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        fragment.findViewById(R.id.card_many_recipes).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), RecipeListActivity.class);
//                startActivity(intent);
//            }
//        });

    }
}
