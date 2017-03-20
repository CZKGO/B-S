package com.czk.diabetes.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.czk.diabetes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/3/20.
 */
public class ChartCoordinate extends View {
    private int mXLineColor;
    private int mYLineColor;
    private float mXLineWidth;
    private float mYLineWidth;
    /**
     * x轴的坐标点
     */
    private List<Float> xSystemPionts;
    /**
     * y轴的坐标点
     */
    private List<Float> ySystemPionts;

    protected float mChartHeight;
    protected float mChartWidth;
    /**
     * x坐标系的起始点和结束点
     */
    private float minX, maxX;
    /**
     * y坐标系的起始点和结束点
     */
    private float minY, maxY;

    public ChartCoordinate(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public ChartCoordinate(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public ChartCoordinate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChartCoordinate(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartCoordinate);
        mXLineColor = a.getColor(R.styleable.ChartCoordinate_xline_color, Color.LTGRAY);
        mYLineColor = a.getColor(R.styleable.ChartCoordinate_yline_color, Color.LTGRAY);
        mXLineWidth = a.getDimension(R.styleable.ChartCoordinate_xline_width, 8);
        mYLineWidth = a.getDimension(R.styleable.ChartCoordinate_yline_width, 8);
        a.recycle();
    }

    /**
     * 设置等分的x坐标轴
     *
     * @param startValue 开始坐标
     * @param endValue   结束坐标
     * @param number     坐标个数
     */
    private void setXSystemPionts(float startValue, float endValue, int number) {
        ArrayList<Float> systemPionts = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            systemPionts.add(startValue + (endValue - startValue) / (number - 1) * i);
        }
        setXSystemPionts(systemPionts);
    }

    /**
     * 设置等分的y坐标轴
     *
     * @param startValue 开始坐标
     * @param endValue   结束坐标
     * @param number     坐标个数
     */
    private void setYSystemPionts(float startValue, float endValue, int number) {
        ArrayList<Float> systemPionts = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            systemPionts.add(startValue + (endValue - startValue) / (number - 1) * i);
        }
        setYSystemPionts(systemPionts);
    }

    /**
     * 设置x坐标轴
     *
     * @param xSystemPionts
     */
    private void setXSystemPionts(ArrayList<Float> xSystemPionts) {
        this.xSystemPionts = xSystemPionts;
        minX = xSystemPionts.get(0);
        maxX = xSystemPionts.get(xSystemPionts.size() - 1);
    }

    /**
     * 设置y坐标轴
     *
     * @param ySystemPionts
     */
    private void setYSystemPionts(ArrayList<Float> ySystemPionts) {
        this.ySystemPionts = ySystemPionts;
        minY = ySystemPionts.get(0);
        maxY = ySystemPionts.get(ySystemPionts.size() - 1);
    }

    /**
     * Paint initialization
     */
    private Paint initPaint(Paint paint, float width, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        return paint;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        drawXSystem(canvas, paint);
        drawYSystem(canvas, paint);
    }

    private void drawYSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mYLineWidth, mYLineColor);
        float startX = mYLineWidth / 2 + getPaddingLeft();
        float startY = mXLineWidth / 2 + getPaddingTop();
        float stopX = mYLineWidth / 2 + getPaddingLeft();
        float stopY = mChartHeight;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawXSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mXLineWidth, mXLineColor);
        float startX = getPaddingLeft();
        float startY = mChartHeight;
        float stopX = mChartWidth;
        float stopY = mChartHeight;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width == 0) {
            width = height;
        }
        if (height == 0) {
            height = width;
        }
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChartWidth = w - mXLineWidth - getPaddingRight();
        mChartHeight = h - mYLineWidth - getPaddingBottom();
    }

}
