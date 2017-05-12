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
import com.czk.diabetes.recipe.AddRecipeActivity;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class MedicineFragment extends Fragment {
    private static final int MEDICINE_QUERY_FINSH = 0;
    private View fragment;
    private TextView tvLeechdomDosage;
    private TextView tvLeechdomName;
    private TextView tvLeechdomTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MEDICINE_QUERY_FINSH:
                    MedicineRecord medicineData = (MedicineRecord) msg.obj;

                    tvLeechdomName.setText(Html.fromHtml(getResources().getString(R.string.designation,medicineData.name)));
                    tvLeechdomDosage.setText(Html.fromHtml(getResources().getString(R.string.dosage,medicineData.doses)));
                    tvLeechdomTime.setText(Html.fromHtml(getResources().getString(R.string.medication_time_colon,medicineData.times)));
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
        initAscnData();
        dealEvent();
    }

    private void initAscnData() {
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(MyApplication.getInstance());
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("medicine_record"
                        , new String[]{"name", "doses", "time", "peroid_start", "peroid_end", "notifition", "description"}
                        , "peroid_end <= ?"
                        , new String[]{TimeUtil.getYearMonthDay(System.currentTimeMillis())}
                        , null, null, "time" + " ASC");
                while (c.moveToNext()) {
                    MedicineRecord data = new MedicineRecord(
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
                Intent intent = new Intent(getActivity(), MedicineActivity.class);
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
