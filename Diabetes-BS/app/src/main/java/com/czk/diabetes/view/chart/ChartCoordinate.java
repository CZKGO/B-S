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

import java.text.DecimalFormat;
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
    /**
     * 需要绘制的坐标点
     */
    protected List<UserPoint> userPionts;

    //图表的起始终止位置
    protected float xStart;
    protected float yStart;
    protected float yEnd;
    protected float xEnd;
    /**
     * x坐标系的起始点和结束点
     */
    protected float minX, maxX;
    /**
     * y坐标系的起始点和结束点
     */
    protected float minY, maxY;
    /**
     * 刻度属性
     */
    private float mScaleWidth;
    private int mScaleColor;
    private float mScaleLength;
    private boolean drawText = true;
    private int coordTextSize = 0;

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
        mScaleColor = a.getColor(R.styleable.ChartCoordinate_scale_color, Color.LTGRAY);
        mScaleWidth = a.getDimension(R.styleable.ChartCoordinate_scale_width, 1);
        mScaleLength = a.getDimension(R.styleable.ChartCoordinate_scale_length, 5);
        a.recycle();

        setXSystemPionts(0, 6, 7);
        setYSystemPionts(0, 6, 7);
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
     * @param userPionts
     */
    public void setUserPionts(List<UserPoint> userPionts) {
        this.userPionts = userPionts;
    }


    public void setUserPionts(List<UserPoint> userPionts, boolean coordinateAutoX, boolean coordinateAutoY, float intervalX, float intervalY) {
        setUserPionts(userPionts);
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        for (UserPoint point : userPionts) {
            if (coordinateAutoX) {
                if (point.x > maxX) {
                    maxX = point.x;
                }
                if (point.x < minX) {
                    minX = point.x;
                }
            }
            if (coordinateAutoY) {
                if (point.y > maxY) {
                    maxY = point.y;
                }

                if (point.y < minY) {
                    minY = point.y;
                }
            }
        }
        if (coordinateAutoX) {
            maxX = (int) maxX + intervalX;
            minX = (int) minX;
//            setXSystemPionts(minX,maxX, (int) (maxX - minX)+1);
            setXSystemPionts(0, maxX, (int) maxX + 1);
        }
        if (coordinateAutoY) {
            maxY = (int) maxY + intervalY;
            minY = (int) minY;
//            setYSystemPionts(minY,maxY, (int) (maxY - minY)+1);
            setYSystemPionts(0, maxY, (int) maxY + 1);
        }
    }

    /**
     * 设置等分的x坐标轴
     *
     * @param startValue 开始坐标
     * @param endValue   结束坐标
     * @param number     坐标个数
     */
    public void setXSystemPionts(float startValue, float endValue, int number) {
        ArrayList<Float> systemPionts = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            DecimalFormat df = new DecimalFormat(".0");
            systemPionts.add(Float.valueOf(df.format(startValue + (endValue - startValue + 1) / number * i)));
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
    public void setYSystemPionts(float startValue, float endValue, int number) {
        ArrayList<Float> systemPionts = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            DecimalFormat df = new DecimalFormat(".0");
            systemPionts.add(Float.valueOf(df.format(startValue + (endValue - startValue + 1) / number * i)));
        }
        setYSystemPionts(systemPionts);
    }

    public void setLineColor(int mLineColor) {
        this.mXLineColor = this.mYLineColor = mScaleColor = mLineColor;
    }

    /**
     * 设置x坐标轴
     *
     * @param xSystemPionts
     */
    public void setXSystemPionts(ArrayList<Float> xSystemPionts) {
        this.xSystemPionts = xSystemPionts;
        minX = xSystemPionts.get(0);
        maxX = xSystemPionts.get(xSystemPionts.size() - 1);
    }

    /**
     * 设置坐标文字大小
     *
     * @param size
     */
    public void setCoordTextSize(int size) {
        coordTextSize = size;
    }

    /**
     * 设置y坐标轴
     *
     * @param ySystemPionts
     */
    private void setYSystemPionts(ArrayList<Float> ySystemPionts) {
        if (ySystemPionts.size() > 0) {
            this.ySystemPionts = ySystemPionts;
            minY = ySystemPionts.get(0);
            maxY = ySystemPionts.get(ySystemPionts.size() - 1);
        }
    }

    /**
     * Paint initialization
     */
    private Paint initPaint(Paint paint, float width, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        return paint;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (drawText) {
            drawXText(canvas, paint);
            drawYText(canvas, paint);
        }
        drawXSystem(canvas, paint);
        drawYSystem(canvas, paint);
        drawScale(canvas, paint);
    }

    private void drawYSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mYLineWidth, mYLineColor);
        canvas.drawLine(xStart, yStart, xStart, yEnd, paint);
    }

    private void drawXSystem(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mXLineWidth, mXLineColor);
        canvas.drawLine(xStart, yEnd, xEnd, yEnd, paint);
    }

    private void drawScale(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mScaleWidth, mScaleColor);
        for (int i = 0; i < ySystemPionts.size(); i++) {
            canvas.drawLine(xStart, getCoordinateY(ySystemPionts.get(i)), xStart + mScaleLength, getCoordinateY(ySystemPionts.get(i)), paint);
        }
        for (int i = 0; i < xSystemPionts.size(); i++) {
            canvas.drawLine(getCoordinateX(xSystemPionts.get(i)), yEnd, getCoordinateX(xSystemPionts.get(i)), yEnd - mScaleLength, paint);
        }
    }

    private void drawXText(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mScaleWidth, mScaleColor);
        paint.setTextSize(coordTextSize);
        for (int i = 0; i < xSystemPionts.size(); i++) {
            String text = xSystemPionts.get(i).toString();
            canvas.drawText(text, getCoordinateX(xSystemPionts.get(i)) - coordTextSize * text.length() / 4, yEnd + coordTextSize, paint);
        }
    }

    private void drawYText(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mScaleWidth, mScaleColor);
        paint.setTextSize(coordTextSize);
        for (int i = 1; i < ySystemPionts.size(); i++) {
            canvas.drawText(ySystemPionts.get(i).toString(), xStart + mScaleLength, getCoordinateY(ySystemPionts.get(i)) + coordTextSize / 2, paint);
        }
    }

    /**
     * 根据x的数值获取在坐标系中的位置
     *
     * @param numberX
     * @return
     */
    protected float getCoordinateX(float numberX) {
        return (numberX - minX) / (maxX - minX) * (xEnd - xStart) + xStart;
    }

    /**
     * 根据y的数值获取在坐标系中的位置
     *
     * @param numberY
     * @return
     */
    protected float getCoordinateY(float numberY) {
        return yEnd - (numberY - minY) / (maxY - minY) * (yEnd - yStart);
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
        xStart = mXLineWidth / 2 + getPaddingLeft();
        yStart = mYLineWidth / 2 + getPaddingTop();
        xEnd = w - mXLineWidth / 2 - getPaddingRight();
        yEnd = h - mYLineWidth / 2 - getPaddingBottom() - coordTextSize;
    }

    public static class UserPoint {
        float x;
        float y;

        public UserPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
