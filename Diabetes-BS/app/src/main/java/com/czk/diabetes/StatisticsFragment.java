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
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.view.chart.BarChartView;
import com.czk.diabetes.view.chart.ChartCoordinate;
import com.czk.diabetes.view.chart.LineChartView;
import com.czk.diabetes.view.chart.PieChartView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class StatisticsFragment extends Fragment {
    private final static int SELECT_FINSH = 0;
    private View fragment;
    private TextView tvLow;
    private TextView tvHigh;
    private TextView tvSafe;
    private TextView tvTotal;
    private TextView tvWaring;
    private PieChartView pieChart;
    private LineChartView lineChart;
    private BarChartView barChart;
    private PieData pieData;
    private Map<String, LineData> lineDataMap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SELECT_FINSH:
                    //饼状图
                    if(null == getActivity()||getActivity().isFinishing()){
                        return;
                    }
                    tvLow.setText(Html.fromHtml(String.format(getResources().getString(R.string.low), pieData.low)));
                    tvHigh.setText(Html.fromHtml(String.format(getResources().getString(R.string.high), pieData.high)));
                    tvSafe.setText(Html.fromHtml(String.format(getResources().getString(R.string.fine), pieData.safe)));
                    tvWaring.setText(Html.fromHtml(String.format(getResources().getString(R.string.warning), pieData.waring)));
                    tvTotal.setText(Html.fromHtml(String.format(getResources().getString(R.string.total), pieData.total)));
                    pieChart.invalidate();
                    if (pieData.total != 0) {
                        List<PieChartView.DataOfPie> datasOfPie = new ArrayList<>();
                        if (pieData.low != 0) {
                            PieChartView.DataOfPie dataLow = new PieChartView.DataOfPie(pieData.low, getResources().getColor(R.color.low_color), new DecimalFormat("#").format((float) pieData.low / (float) pieData.total * 100) + "%");
                            datasOfPie.add(dataLow);
                        }
                        if (pieData.safe != 0) {
                            PieChartView.DataOfPie dataSafe = new PieChartView.DataOfPie(pieData.safe, getResources().getColor(R.color.safe_color), new DecimalFormat("#").format((float) pieData.safe / (float) pieData.total * 100) + "%");
                            datasOfPie.add(dataSafe);
                        }
                        if (pieData.high != 0) {
                            PieChartView.DataOfPie dataHigh = new PieChartView.DataOfPie(pieData.high, getResources().getColor(R.color.warning_color), new DecimalFormat("#").format((float) pieData.high / (float) pieData.total * 100) + "%");
                            datasOfPie.add(dataHigh);
                        }
                        if (pieData.waring != 0) {
                            PieChartView.DataOfPie dataWaring = new PieChartView.DataOfPie(pieData.waring, getResources().getColor(R.color.eorr_color), new DecimalFormat("#").format((float) pieData.waring / (float) pieData.total * 100) + "%");
                            datasOfPie.add(dataWaring);
                        }
                        pieChart.setDrawPercentage(true);
                        pieChart.setDatasAndColors(datasOfPie);
                    }
                    //折线图
                    if (null != lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis()))) {
                        lineChart.setPointColor(ThemeUtil.getThemeColor());
                        lineChart.setLineColor(ThemeUtil.getThemeColor());
                        float[] values = lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis())).values;
                        List<LineChartView.UserPoint> userPoints = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            if (values[i] != -1) {
                                LineChartView.UserPoint userPoint = new LineChartView.UserPoint(i, values[i]);
                                userPoints.add(userPoint);
                            }
                        }
                        lineChart.setUserPionts(userPoints,true,true,1,1);
                        lineChart.invalidate();
                    }
                    //柱状图
                    if (null != lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis()))) {
                        barChart.setBarOneColor(ThemeUtil.getThemeColor());
                        barChart.setBarTowColor(ThemeUtil.getThemeColorLight());
                        float[] values = lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis())).values;
                        List<LineChartView.UserPoint> userPoints = new ArrayList<>();
                        if (null != lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
                            userPoints.add(new ChartCoordinate.UserPoint(0, lineDataMap.get(TimeUtil.getYearMonthDay(System.currentTimeMillis() - 24 * 60 * 60 * 1000)).values[7]));
                        }
                        for (int i = 0; i < values.length - 1; i++) {
                            ChartCoordinate.UserPoint userPoint = new ChartCoordinate.UserPoint(i + 1, values[i + 1]);
                            userPoints.add(userPoint);
                        }
                        barChart.setUserPionts(userPoints,true,true,1,1);
                        barChart.invalidate();
                    }
                    break;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_statistics, container, false);
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
        barChart = (BarChartView) fragment.findViewById(R.id.bar_chart);

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
