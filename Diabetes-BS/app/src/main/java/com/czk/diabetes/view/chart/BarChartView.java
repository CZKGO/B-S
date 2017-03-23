package com.czk.diabetes.view.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.czk.diabetes.R;

/**
 * Created by 陈忠凯 on 2017/3/20.
 */
public class BarChartView extends ChartCoordinate {
    private int barOneColor;
    private int barTowColor;
    private float barWidth;
    private float barInterval;

    public void setBarTowColor(int barTowColor) {
        this.barTowColor = barTowColor;
    }

    public void setBarOneColor(int barOneColor) {
        this.barOneColor = barOneColor;
    }


    public BarChartView(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BarChartView);
        barOneColor = a.getColor(R.styleable.BarChartView_bar_one_color, Color.GRAY);
        barTowColor = a.getColor(R.styleable.BarChartView_bar_tow_color, Color.LTGRAY);
        barWidth = a.getDimension(R.styleable.BarChartView_bar_width, 16);
        barInterval = a.getDimension(R.styleable.BarChartView_bar_interval, 4);
        a.recycle();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        drawPointBar(canvas, paint);
    }

    private void drawPointBar(Canvas canvas, Paint paint) {
        for (int i = 1; i < userPionts.size() / 2 + 1; i++) {
            paint.setStrokeWidth(barWidth);
            paint.setStrokeCap(Paint.Cap.SQUARE);
            paint.setColor(barOneColor);
            if (userPionts.get((i - 1) * 2).y != -1) {
                canvas.drawLine(getCoordinateX(i) - barWidth - barInterval / 2,
                        getCoordinateY(userPionts.get((i - 1) * 2).y),
                        getCoordinateX(i) - barWidth - barInterval / 2,
                        yEnd - barWidth / 2,
                        paint);
            }

            if (userPionts.get((i - 1) * 2 + 1).y != -1) {
                paint.setColor(barTowColor);
                canvas.drawLine(getCoordinateX(i) + barWidth + barInterval / 2,
                        getCoordinateY(userPionts.get((i - 1) * 2+ 1).y),
                        getCoordinateX(i) + barWidth + barInterval / 2,
                        yEnd - barWidth / 2,
                        paint);
            }
        }
    }

}
