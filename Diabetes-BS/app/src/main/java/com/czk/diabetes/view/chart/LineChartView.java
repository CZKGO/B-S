package com.czk.diabetes.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import com.czk.diabetes.R;

/**
 * Created by xuezaishao on 2017/3/9.
 */

public class LineChartView extends ChartCoordinate {
    private int mLineColor;
    private float mLineWidth;
    private float pointRadius;
    private int pointColor;

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
        mLineColor = a.getColor(R.styleable.LineChartView_line_color, Color.LTGRAY);
        mLineWidth = a.getDimension(R.styleable.LineChartView_line_width, 8);
        pointColor = a.getColor(R.styleable.LineChartView_point_color, Color.LTGRAY);
        pointRadius = a.getDimension(R.styleable.LineChartView_point_radius, 8);
        a.recycle();

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
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        drawPointLine(canvas, paint);
        drawPoint(canvas, paint);
    }

    private void drawPointLine(Canvas canvas, Paint paint) {
        paint = initPaint(paint, mLineWidth, mLineColor);
        for (int i = 1; i < userPionts.size(); i++) {
            canvas.drawLine((userPionts.get(i - 1).x - minX) / (maxX - minX) * (xEnd - xStart) + xStart,
                    yEnd - (userPionts.get(i - 1).y - minY) / (maxY - minY) * (yEnd - yStart),
                    (userPionts.get(i).x - minX) / (maxX - minX) * (xEnd - xStart) + xStart,
                    yEnd - (userPionts.get(i).y - minY) / (maxY - minY) * (yEnd - yStart),
                    paint);
        }

    }

    private void drawPoint(Canvas canvas, Paint paint) {
        paint.setColor(pointColor);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < userPionts.size(); i++) {
            canvas.drawCircle((userPionts.get(i).x - minX) / (maxX - minX) * (xEnd - xStart) + xStart,
                    yEnd - (userPionts.get(i).y - minY) / (maxY - minY) * (yEnd - yStart),
                    pointRadius,
                    paint);
        }

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

    public void setLineColor(int lineColor) {
        this.mLineColor = lineColor;
    }


}
