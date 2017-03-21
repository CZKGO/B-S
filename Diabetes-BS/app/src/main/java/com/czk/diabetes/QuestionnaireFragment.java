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
import com.czk.diabetes.data.ConfigureData;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.view.chart.LineChartView;
import com.czk.diabetes.view.chart.PieChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class QuestionnaireFragment extends Fragment {
    private final static int SELECT_FINSH = 0;
    private View fragment;
    private TextView tvLow;
    private TextView tvHigh;
    private TextView tvSafe;
    private TextView tvTotal;
    private TextView tvWaring;
    private PieChartView pieChart;
    private LineChartView lineChart;
    private PieData pieData;
    private Map<String, LineData> lineDataMap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SELECT_FINSH:
                    //饼状图
                    tvLow.setText(Html.fromHtml(String.format(getResources().getString(R.string.low), pieData.low)));
                    tvHigh.setText(Html.fromHtml(String.format(getResources().getString(R.string.high), pieData.high)));
                    tvSafe.setText(Html.fromHtml(String.format(getResources().getString(R.string.fine), pieData.safe)));
                    tvWaring.setText(Html.fromHtml(String.format(getResources().getString(R.string.warning), pieData.waring)));
                    tvTotal.setText(Html.fromHtml(String.format(getResources().getString(R.string.total), pieData.total)));
                    pieChart.invalidate();
                    if (pieData.total != 0) {
                        List<PieChartView.DataOfPie> datasOfPie = new ArrayList<>();
                        PieChartView.DataOfPie dataLow = new PieChartView.DataOfPie(pieData.low, getResources().getColor(R.color.low_color));
                        datasOfPie.add(dataLow);
                        PieChartView.DataOfPie dataSafe = new PieChartView.DataOfPie(pieData.safe, getResources().getColor(R.color.safe_color));
                        datasOfPie.add(dataSafe);
                        PieChartView.DataOfPie dataHigh = new PieChartView.DataOfPie(pieData.high, getResources().getColor(R.color.warning_color));
                        datasOfPie.add(dataHigh);
                        PieChartView.DataOfPie dataWaring = new PieChartView.DataOfPie(pieData.waring, getResources().getColor(R.color.eorr_color));
                        datasOfPie.add(dataWaring);
                        pieChart.setDatasAndColors(datasOfPie);
                    }
                    //折线图
                    if (null != lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis()))) {
                        lineChart.setLineColor(getResources().getColor(R.color.theme_color));
                        lineChart.setXSystemPionts(0, 8, 8);
                        lineChart.setYSystemPionts(ConfigureData.MIN_VALUE, ConfigureData.MAX_VALUE, (int) ConfigureData.MAX_VALUE);
                        float[] values = lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis())).values;
                        List<LineChartView.UserPoint> userPoints = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            if (values[i] != -1) {
                                LineChartView.UserPoint userPoint = new LineChartView.UserPoint(i, values[i]);
                                userPoints.add(userPoint);
                            }
                        }
                        lineChart.setUserPionts(userPoints);
                        lineChart.invalidate();
                    }
                    break;
            }
        }
    };

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
        lineChart = (LineChartView) fragment.findViewById(R.id.line_chart);

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
                lineDataMap = new HashMap();
                while (c.moveToNext()) {
                    BloodSugarData data = new BloodSugarData(
                            c.getString(c.getColumnIndex("date")),
                            c.getInt(c.getColumnIndex("time_slot")),
                            c.getFloat(c.getColumnIndex("value")));
                    //饼状图数据
                    pieData.total = pieData.total + 1;
                    if (data.value < ConfigureData.THRESHOLD_LOW_SAFE) {
                        pieData.low = pieData.low + 1;
                    } else if (data.value < ConfigureData.THRESHOLD_SAFE_HIGH) {
                        pieData.safe = pieData.safe + 1;
                    } else if (data.value < ConfigureData.THRESHOLD_HIGH_WARING) {
                        pieData.high = pieData.high + 1;
                    } else {
                        pieData.waring = pieData.waring + 1;
                    }
                    //折线图数据
                    if (lineDataMap.containsKey(data.date)) {
                        lineDataMap.get(data.date).values[data.timeSlot] = data.value;
                    } else {
                        LineData lineData = new LineData();
                        lineData.values[data.timeSlot] = data.value;
                        lineDataMap.put(data.date, lineData);
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
        private final int timeSlot;
        private final float value;

        public BloodSugarData(String date, int timeSlot, float value) {
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

    private class LineData {
        public float[] values = new float[]{-1, -1, -1, -1, -1, -1, -1, -1};
    }
}
