package com.czk.diabetes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.DB.DBOpenHelper;
import com.czk.diabetes.athletic.AddAthleticActivity;
import com.czk.diabetes.medicine.MedicineActivity;
import com.czk.diabetes.medicine.MedicineRecord;
import com.czk.diabetes.recipe.EatRecord;
import com.czk.diabetes.recipe.RecipeActivity;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class OtherFunctionFragment extends Fragment {
    private static final int MEDICINE_QUERY_FINSH = 0;
    private static final int FOOD_QUERY_FINSH = 1;
    private View fragment;
    private TextView tvLeechdomDosage;
    private TextView tvLeechdomName;
    private TextView tvLeechdomTime;
    private TextView tvDietName;
    private TextView tvSugarChange;
    private TextView tvDietTime;
    private TextView tvAthleticsName;
    private TextView tvAthleticsSugarChange;
    private TextView tvAthleticsTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MEDICINE_QUERY_FINSH:
                    MedicineRecord medicineData = (MedicineRecord) msg.obj;
                    tvLeechdomName.setText(Html.fromHtml(getResources().getString(R.string.designation,medicineData.name)));
                    tvLeechdomDosage.setText(Html.fromHtml(getResources().getString(R.string.dosage,medicineData.doses))+getString(R.string.mg));
                    tvLeechdomTime.setText(Html.fromHtml(getResources().getString(R.string.medication_time_colon,medicineData.time)));
                    break;
                case FOOD_QUERY_FINSH:
                    EatRecord eatData = (EatRecord) msg.obj;
                    tvDietName.setText(Html.fromHtml(getResources().getString(R.string.designation,eatData.name)));
                    float dValue = Float.parseFloat(eatData.afterDining) - Float.parseFloat(eatData.beforeDining);
                    String value = "";
                    if(dValue<0){
                        value = "-"+Math.abs(dValue);
                    }else {
                        value = "+"+dValue;
                    }
                    tvSugarChange.setText(Html.fromHtml(getResources().getString(R.string.dosage,value))+getString(R.string.mmol_l));
                    tvDietTime.setText(Html.fromHtml(getResources().getString(R.string.diet_time,eatData.type)));
                    break;
            }
        }
    };

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

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(MyApplication.getInstance());
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("medicine_record"
                        , new String[]{"add_time","name", "doses", "time", "peroid_start", "peroid_end", "notifition", "description"}
                        , "peroid_end >= ? and peroid_start <= ? and time >= ?"
                        , new String[]{TimeUtil.getYearMonthDay(System.currentTimeMillis())
                                , TimeUtil.getYearMonthDay(System.currentTimeMillis())
                                ,TimeUtil.getSringByFormat(System.currentTimeMillis(),"HH:mm")}
                        , null
                        , null
                        , "time" + " ASC");
                while (c.moveToNext()) {
                    MedicineRecord data = new MedicineRecord(
                            c.getString(c.getColumnIndex("add_time")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("doses")),
                            c.getString(c.getColumnIndex("time")),
                            c.getString(c.getColumnIndex("peroid_start")),
                            c.getString(c.getColumnIndex("peroid_end")),
                            c.getString(c.getColumnIndex("notifition")),
                            c.getString(c.getColumnIndex("description")));
                    Message msg = new Message();
                    msg.what = MEDICINE_QUERY_FINSH;
                    msg.obj = data;
                    handler.sendMessage(msg);
                    break;
                }
                c.close();
                db.close();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(MyApplication.getInstance());
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("eat_record"
                        , new String[]{"add_time", "name", "material", "type", "time", "before_dining", "after_dining", "description"}
                        , "add_time <= ?"
                        , new String[]{String.valueOf(System.currentTimeMillis())}
                        , null
                        , null
                        , "add_time" + " DESC");
                while (c.moveToNext()) {
                    EatRecord data =  new EatRecord(
                            c.getString(c.getColumnIndex("add_time")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("material")),
                            c.getString(c.getColumnIndex("type")),
                            c.getString(c.getColumnIndex("time")),
                            c.getString(c.getColumnIndex("before_dining")),
                            c.getString(c.getColumnIndex("after_dining")),
                            c.getString(c.getColumnIndex("description")));
                    Message msg = new Message();
                    msg.what = FOOD_QUERY_FINSH;
                    msg.obj = data;
                    handler.sendMessage(msg);
                    break;
                }
                c.close();
                db.close();
            }
        }).start();
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
        tvLeechdomName = (TextView) fragment.findViewById(R.id.txt_leechdom_name);
        tvLeechdomDosage = (TextView) fragment.findViewById(R.id.txt_leechdom_doses);
        tvLeechdomTime = (TextView) fragment.findViewById(R.id.txt_leechdom_time);
        tvLeechdomName.setText(Html.fromHtml(getResources().getString(R.string.designation,getString(R.string.unknown))));
        tvLeechdomDosage.setText(Html.fromHtml(getResources().getString(R.string.dosage,getString(R.string.unknown))));
        tvLeechdomTime.setText(Html.fromHtml(getResources().getString(R.string.medication_time_colon,getString(R.string.unknown))));


        /**************************************************
         * 饮食                                           *
         **************************************************/
        ImageView dietIconIV = (ImageView) fragment.findViewById(R.id.diet_icon);
        FontIconDrawable dietIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_spoon_knife);
        dietIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        dietIconIV.setImageDrawable(dietIconDrawable);
        ImageView dietLeftIconIV = (ImageView) fragment.findViewById(R.id.diet_left_icon);
        dietLeftIconIV.setImageDrawable(dietIconDrawable);
        tvDietName = (TextView) fragment.findViewById(R.id.txt_diet_name);
        tvSugarChange = (TextView) fragment.findViewById(R.id.txt_diet_blood_sugar);
        tvDietTime = (TextView) fragment.findViewById(R.id.txt_diet_time);
        tvDietName.setText(Html.fromHtml(getResources().getString(R.string.designation,getString(R.string.unknown))));
        tvDietTime.setText(Html.fromHtml(getResources().getString(R.string.diet_time,getString(R.string.unknown))));
        tvSugarChange.setText(Html.fromHtml(getResources().getString(R.string.blood_sugar_change,getString(R.string.unknown))));
        /**************************************************
         * 运动                                           *
         **************************************************/
        ImageView athleticsIconIV = (ImageView) fragment.findViewById(R.id.athletics_icon);
        FontIconDrawable athleticsIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_directions_run);
        athleticsIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        athleticsIconIV.setImageDrawable(athleticsIconDrawable);
        ImageView athleticsLeftIconIV = (ImageView) fragment.findViewById(R.id.athletics_left_icon);
        athleticsLeftIconIV.setImageDrawable(athleticsIconDrawable);
        tvAthleticsName = (TextView) fragment.findViewById(R.id.txt_athletics_name);
        tvAthleticsSugarChange = (TextView) fragment.findViewById(R.id.txt_athletics_time);
        tvAthleticsTime = (TextView) fragment.findViewById(R.id.txt_athletics_consume);
        tvAthleticsName.setText(Html.fromHtml(getResources().getString(R.string.designation,getString(R.string.unknown))));
        tvAthleticsSugarChange.setText(Html.fromHtml(getResources().getString(R.string.athletics_time,getString(R.string.unknown))));
        tvAthleticsTime.setText(Html.fromHtml(getResources().getString(R.string.blood_sugar_change,getString(R.string.unknown))));

    }

    private void dealEvent() {
        fragment.findViewById(R.id.card_leechdom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);
            }
        });

        fragment.findViewById(R.id.card_diet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
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
