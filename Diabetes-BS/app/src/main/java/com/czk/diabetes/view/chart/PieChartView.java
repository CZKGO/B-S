package com.czk.diabetes.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.czk.diabetes.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/3/10.
 */
public class PieChartView extends View {

    private float startAngle = 90;
    private List<DataOfPie> datas;
    private float total;
    private RectF mPieRectF = new RectF();
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    private int txtColor;
    private float txtSize;
    private boolean isDrawPercentage;

    public PieChartView(Context context) {
        super(context);
        init(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
        startAngle = a.getFloat(R.styleable.PieChartView_start_angle, 90);
        txtColor = a.getColor(R.styleable.PieChartView_txt_color, Color.WHITE);
        txtSize = a.getDimension(R.styleable.PieChartView_txt_size, 16);
        isDrawPercentage = a.getBoolean(R.styleable.PieChartView_draw_percentage,false);
        a.recycle();

        List<DataOfPie> datas = new ArrayList<>();
        DataOfPie data1 = new DataOfPie(6f, Color.LTGRAY, new DecimalFormat("#").format(6f / 7f * 100) + "%");
        DataOfPie data2 = new DataOfPie(1f, Color.GRAY, new DecimalFormat("#").format(1f / 7f * 100) + "%");
        datas.add(data1);
        datas.add(data2);
        setDatasAndColors(datas);
    }

    public void setDatasAndColors(List<DataOfPie> datas) {
        this.datas = datas;
        total = 0;
        for (DataOfPie data : datas) {
            total += data.portion;
        }
    }

    public void setDrawPercentage(boolean drawPercentage) {
        isDrawPercentage = drawPercentage;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        float sweepAngle = 0;
        float startAngle = this.startAngle;
        for (DataOfPie data : datas) {
            paint.setColor(data.corlor);
            sweepAngle = 360.0f * data.portion / total;
            canvas.drawArc(mPieRectF, startAngle, sweepAngle, true, paint);
            if (isDrawPercentage)
                drawText(canvas, paint, sweepAngle, startAngle, data);
            startAngle = sweepAngle + startAngle;
        }
    }

    private void drawText(Canvas canvas, Paint paint, float sweepAngle, float startAngle, DataOfPie data) {
        paint.setColor(txtColor);
        paint.setTextSize(txtSize);
        float textWidth = paint.measureText(data.text);
        double textAngle = (sweepAngle / 2 + startAngle) / 180 * Math.PI;
        canvas.drawText(data.text
                , (float) (mPieRectF.centerX() + Math.cos(textAngle) * mRadius / 2 - textWidth / 2)
                , (float) (mPieRectF.centerY() + Math.sin(textAngle) * mRadius / 2 + txtSize / 2)
                , paint);
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

    /**
     * When the size of CircleProgressBar changed, need to re-adjust the drawing area
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;

        mRadius = Math.min(mCenterX - getPaddingLeft(), mCenterY - getPaddingTop());
        mPieRectF.top = mCenterY - mRadius;
        mPieRectF.bottom = mCenterY + mRadius;
        mPieRectF.left = mCenterX - mRadius;
        mPieRectF.right = mCenterX + mRadius;

    }


    public static class DataOfPie {
        String text;
        float portion;
        int corlor;

        public DataOfPie(float portion, int corlor, String text) {
            this.portion = portion;
            this.corlor = corlor;
            this.text = text;
        }
    }
}
