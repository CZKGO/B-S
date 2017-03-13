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

import java.util.ArrayList;
import java.util.List;

import com.czk.diabetes.R;

/**
 * Created by xuezaishao on 2017/3/9.
 */

public class LineChartView extends View {
    private int mLineColor = Color.BLACK;
    private int mXLineColor = Color.BLACK;
    private int mYLineColor = Color.BLACK;
    private float mLineWidth = 8;
    private float mXLineWidth = 8;
    private float mYLineWidth = 8;
    /**
     * x轴的坐标点
     */
    private List<Float> xSystemPionts;
    /**
     * y轴的坐标点
     */
    private List<Float> ySystemPionts;
    /**
     * 需要绘制的坐标点
     */
    private List<UserPoint> userPionts;

    private float mChartHeight;
    private float mChartWidth;
    /**
     * x坐标系的起始点和结束点
     */
    private float minX, maxX;
    /**
     * y坐标系的起始点和结束点
     */
    private float minY, maxY;

    public LineChartView(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineChartView);
        mLineColor = a.getColor(R.styleable.LineChartView_line_color, Color.BLACK);
        mXLineColor = a.getColor(R.styleable.LineChartView_xline_color, Color.BLACK);
        mYLineColor = a.getColor(R.styleable.LineChartView_yline_color, Color.BLACK);
        mLineWidth = a.getDimension(R.styleable.LineChartView_line_width, 8);
        mXLineWidth = a.getDimension(R.styleable.LineChartView_xline_width, 8);
        mYLineWidth = a.getDimension(R.styleable.LineChartView_yline_width, 8);
        a.recycle();

        setXSystemPionts(0, 5, 6);
        setYSystemPionts(0, 5, 6);
        List<UserPoint> userPionts = new ArrayList<>();
        UserPoint point = new UserPoint(1f, 4f);
        UserPoint point2 = new UserPoint(2f, 2f);
        UserPoint point3 = new UserPoint(3f, 4f);
        UserPoint point4 = new UserPoint(4f, 2f);
        userPionts.add(point);
        userPionts.add(point2);
        userPionts.add(point3);
        userPionts.add(point4);
        setUserPionts(userPionts);
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
     * @param userPionts
     */
    private void setUserPionts(List<UserPoint> userPionts) {
        this.userPionts = userPionts;
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
        drawPointLine(canvas, paint);
    }

    private void drawXSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mXLineWidth, mXLineColor);
        canvas.drawLine(mXLineWidth / 2, mYLineWidth / 2, mXLineWidth / 2, mChartHeight, paint);
    }

    private void drawYSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mXLineWidth, mXLineColor);
        canvas.drawLine(0, mChartHeight, mChartWidth, mChartHeight, paint);
    }

    private void drawPointLine(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mLineWidth, mLineColor);
        for (int i = 1; i < userPionts.size(); i++) {
            canvas.drawLine((userPionts.get(i - 1).x - minX) / (maxX - minX) * mChartHeight,
                    (userPionts.get(i - 1).y - minY) / (maxY - minY) * mChartWidth,
                    (userPionts.get(i).x - minX) / (maxX - minX) * mChartHeight,
                    (userPionts.get(i).y - minY) / (maxY - minY) * mChartWidth,
                    paint);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * When the size of CircleProgressBar changed, need to re-adjust the drawing area
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChartWidth = w - mXLineWidth / 2;
        mChartHeight = h - mYLineWidth / 2;
    }

    private class UserPoint {
        float x;
        float y;

        public UserPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
