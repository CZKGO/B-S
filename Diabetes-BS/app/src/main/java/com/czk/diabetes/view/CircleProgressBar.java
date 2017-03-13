package com.czk.diabetes.view;

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

/**
 * Created by 陈忠凯 on 2017/3/8.
 */
public class CircleProgressBar extends View {
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mProgressColor = 0xFFF2A670;
    private int mProgressBackgroundColor = 0xFFD3D3D5;
    private float mProgressWidth = 8;
    private float mProgressStartAngle = 0;
    private float progressValue = 50;
    private float MaxProgressValue = 100;
    private RectF mProgressRectF = new RectF();
    private float mCenterX;
    private float mCenterY;
    private float mRadius;

    public CircleProgressBar(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        mBackgroundColor = a.getColor(R.styleable.CircleProgressBar_background_color, Color.TRANSPARENT);
        mProgressColor = a.getColor(R.styleable.CircleProgressBar_progress_color, 0xFFF2A670);
        mProgressBackgroundColor = a.getColor(R.styleable.CircleProgressBar_progress_background_color, 0xFFD3D3D5);
        mProgressWidth = a.getDimension(R.styleable.CircleProgressBar_progress_width, 8);
        mProgressStartAngle = a.getFloat(R.styleable.CircleProgressBar_start_angle, 0);
        progressValue = a.getFloat(R.styleable.CircleProgressBar_value, 50);
        MaxProgressValue = a.getFloat(R.styleable.CircleProgressBar_max_value, 100);
        a.recycle();
    }

    /**
     * Paint initialization
     */
    private Paint initProgeessPaint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mProgressWidth);
        paint.setColor(mProgressColor);
        return paint;
    }

    private Paint initProgeessBackgroundPaint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mProgressWidth);
        paint.setColor(mProgressBackgroundColor);
        return paint;
    }

    private Paint initBackgroundPaint(Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        return paint;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        drawBackground(canvas, paint);
        drawProgress(canvas, paint);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        paint = initBackgroundPaint(paint);
        if (mBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(mCenterX, mCenterX, mRadius, paint);
        }
    }

    /**
     * Just draw arc
     */
    private void drawProgress(Canvas canvas, Paint paint) {
        paint = initProgeessBackgroundPaint(paint);
        canvas.drawArc(mProgressRectF, 0.0f, 360.0f, false, paint);
        paint = initProgeessPaint(paint);
        canvas.drawArc(mProgressRectF, mProgressStartAngle, 360.0f * getProgress() / getMax(), false, paint);
    }
    public void setProgress(float progressValue) {
        this.progressValue = progressValue;
        invalidate();
    }
    public float getProgress() {
        return progressValue;
    }

    public float getMax() {
        return MaxProgressValue;
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
        mProgressRectF.top = mCenterY - mRadius;
        mProgressRectF.bottom = mCenterY + mRadius;
        mProgressRectF.left = mCenterX - mRadius;
        mProgressRectF.right = mCenterX + mRadius;

        //Prevent the progress from clipping
        mProgressRectF.inset(mProgressWidth / 2, mProgressWidth / 2);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        invalidate();
    }

    public void setProgressWidth(float progressWidth) {
        this.mProgressWidth = progressWidth;
        mProgressRectF.inset(mProgressWidth / 2, mProgressWidth / 2);
        invalidate();
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        invalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressBackgroundColor(int mProgressBackgroundColor) {
        this.mProgressBackgroundColor = mProgressBackgroundColor;
        invalidate();
    }

    public int getProgressBackgroundColor() {
        return mProgressBackgroundColor;
    }
}
