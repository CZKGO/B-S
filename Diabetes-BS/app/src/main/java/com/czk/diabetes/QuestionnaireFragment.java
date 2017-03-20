package com.czk.diabetes;

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
import com.czk.diabetes.data.BloodSugarLevel;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.view.chart.PieChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class QuestionnaireFragment extends Fragment {
    private View fragment;
    private final static int SELECT_FINSH = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SELECT_FINSH:
                    List<PieChartView.DataOfPie> datasOfPie = new ArrayList<>();
                    PieChartView.DataOfPie dataLow = new PieChartView.DataOfPie(pieData.low, getResources().getColor(R.color.low_color));
                    datasOfPie.add(dataLow);
                    PieChartView.DataOfPie dataSafe = new PieChartView.DataOfPie(pieData.safe, getResources().getColor(R.color.safe_color));
                    datasOfPie.add(dataSafe);
                    PieChartView.DataOfPie dataHigh = new PieChartView.DataOfPie(pieData.high, getResources().getColor(R.color.warning_color));
                    datasOfPie.add(dataHigh);
                    PieChartView.DataOfPie dataWaring = new PieChartView.DataOfPie(pieData.waring, getResources().getColor(R.color.eorr_color));
                    datasOfPie.add(dataWaring);
                    tvLow.setText(Html.fromHtml(String.format(getResources().getString(R.string.low), pieData.low)));
                    tvHigh.setText(Html.fromHtml(String.format(getResources().getString(R.string.high), pieData.high)));
                    tvSafe.setText(Html.fromHtml(String.format(getResources().getString(R.string.fine), pieData.safe)));
                    tvWaring.setText(Html.fromHtml(String.format(getResources().getString(R.string.warning), pieData.waring)));
                    tvTotal.setText(Html.fromHtml(String.format(getResources().getString(R.string.total), pieData.total)));
                    pieChart.setDatasAndColors(datasOfPie);
                    pieChart.invalidate();
                    break;
            }
        }
    };
    private PieData pieData;
    private PieChartView pieChart;
    private TextView tvLow;
    private TextView tvHigh;
    private TextView tvSafe;
    private TextView tvTotal;
    private TextView tvWaring;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_questionnaire, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void initView() {
        /**************************************************
         * 分布                                           *
         **************************************************/
        //标题
        ImageView pieChartIconIV = (ImageView) fragment.findViewById(R.id.pie_chart_icon);
        FontIconDrawable pieChartIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_pie_chart);
        pieChartIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        pieChartIconIV.setImageDrawable(pieChartIconDrawable);
        pieChart = (PieChartView) fragment.findViewById(R.id.pie_chart);
        tvLow = (TextView) fragment.findViewById(R.id.txt_low);
        tvHigh = (TextView) fragment.findViewById(R.id.txt_high);
        tvSafe = (TextView) fragment.findViewById(R.id.txt_fine);
        tvWaring = (TextView) fragment.findViewById(R.id.txt_waring);
        tvTotal = (TextView) fragment.findViewById(R.id.txt_total);
        /**************************************************
         * 趋势                                           *
         **************************************************/
        //标题
        ImageView lineChartIconIV = (ImageView) fragment.findViewById(R.id.line_chart_icon);
        FontIconDrawable lineChartIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_stats_dots);
        lineChartIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        lineChartIconIV.setImageDrawable(lineChartIconDrawable);

        /**************************************************
         * 对照                                           *
         **************************************************/
        //标题
        ImageView barChartIconIV = (ImageView) fragment.findViewById(R.id.bar_chart_icon);
        FontIconDrawable barChartIconDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_stats_bars);
        barChartIconDrawable.setTextColor(getResources().getColor(R.color.background_white));
        barChartIconIV.setImageDrawable(barChartIconDrawable);

    }

    private void dealEvent() {

    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(getActivity());
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("blood_sugar_record"
                        , new String[]{"date", "time_slot", "value"}
                        , null
                        , null
                        , null, null, null);
                pieData = new PieData();
                while (c.moveToNext()) {
                    BloodSugarData data = new BloodSugarData(
                            c.getString(c.getColumnIndex("date")),
                            c.getString(c.getColumnIndex("time_slot")),
                            c.getFloat(c.getColumnIndex("value")));
                    pieData.total = pieData.total + 1;
                    if (data.value < BloodSugarLevel.THRESHOLD_LOW_SAFE) {
                        pieData.low = pieData.low + 1;
                    } else if (data.value < BloodSugarLevel.THRESHOLD_SAFE_HIGH) {
                        pieData.safe = pieData.safe + 1;
                    } else if (data.value < BloodSugarLevel.THRESHOLD_HIGH_WARING) {
                        pieData.high = pieData.high + 1;
                    } else {
                        pieData.waring = pieData.waring + 1;
                    }
                }
                handler.sendEmptyMessage(SELECT_FINSH);
                c.close();
                db.close();
            }
        }).start();
    }


    private class BloodSugarData {
        private final String date;
        private final String timeSlot;
        private final float value;

        public BloodSugarData(String date, String timeSlot, float value) {
            this.date = date;
            this.timeSlot = timeSlot;
            this.value = value;
        }
    }

    private class PieData {
        public int low;
        public int safe;
        public int high;
        public int waring;
        public int total;
    }
}
