package com.czk.diabetes.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.R;

/**
 * Created by xuezaishao on 2017/3/13.
 * <p/>
 * 作为测量值显示的表盘
 */

public class MeterView extends LinearLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
    private ImageView ivIcon;
    private TextView tvTitle;
    private int titleVisible;
    private int iconVisible;
    private TextView tvValue;
    private CircleProgressBar progressBar;
    private int colorLow;
    private int colorWarning;
    private int colorSafe;
    private int colorEorr;
    private float thresholdSafeLow;
    private float thresholdSafeWarning;
    private float thresholdWarningError;
    private float value;


    public MeterView(Context context) {
        super(context);
        initAtrr(context, null);
        initView();
    }

    public MeterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtrr(context, attrs);
        initView();
    }

    public MeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtrr(context, attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MeterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtrr(context, attrs);
        initView();
    }

    private void initAtrr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MeterView);
        titleVisible = a.getInt(R.styleable.MeterView_title_visible, VISIBLE);
        iconVisible = a.getInt(R.styleable.MeterView_icon_visible, VISIBLE);

        colorLow = a.getInt(R.styleable.MeterView_color_low, getResources().getColor(R.color.low_color));
        colorSafe = a.getInt(R.styleable.MeterView_color_safe, getResources().getColor(R.color.safe_color));
        colorWarning = a.getInt(R.styleable.MeterView_color_warning, getResources().getColor(R.color.warning_color));
        colorEorr = a.getInt(R.styleable.MeterView_color_eorr, getResources().getColor(R.color.eorr_color));

        thresholdSafeLow = a.getFloat(R.styleable.MeterView_threshold_safe_low, 4.4f);
        thresholdSafeWarning = a.getFloat(R.styleable.MeterView_threshold_safe_warning, 10f);
        thresholdWarningError = a.getFloat(R.styleable.MeterView_threshold_warning_eorr, 15f);

        a.recycle();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_meter, this);
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setVisibility(iconVisible);
        tvTitle = (TextView) findViewById(R.id.tile);
        tvTitle.setVisibility(titleVisible);
        tvValue = (TextView) findViewById(R.id.value);
        progressBar = (CircleProgressBar) findViewById(R.id.circular);
    }

    public void setIcon(Drawable icon) {
        ivIcon.setImageDrawable(icon);
    }

    public void setTile(String tile) {
        tvTitle.setText(tile);
    }

    public void setValue(float value) {
        this.value = value;
        tvValue.setText(String.valueOf(value));
//        progressBar.setValue(value);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                progressBar,
                "value",
                progressBar.getValue(),
                value);
        animator.setDuration(1300);
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
        changeColorFromValue(value);
        progressBar.invalidate();
    }

    private void changeColorFromValue(float value) {
        if(value<thresholdSafeLow){
            progressBar.setProgressColor(colorLow);
            tvValue.setTextColor(colorLow);
        }else if(value<thresholdSafeWarning){
            progressBar.setProgressColor(colorSafe);
            tvValue.setTextColor(colorSafe);
        }else if(value<thresholdWarningError){
            progressBar.setProgressColor(colorWarning);
            tvValue.setTextColor(colorWarning);
        }else {
            progressBar.setProgressColor(colorEorr);
            tvValue.setTextColor(colorEorr);
        }
    }

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public float getValue() {
        return value;
    }
}
