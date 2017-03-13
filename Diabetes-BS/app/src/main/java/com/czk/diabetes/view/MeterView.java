package com.czk.diabetes.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.R;

/**
 * Created by xuezaishao on 2017/3/13.
 * <p>
 * 作为测量值显示的表盘
 */

public class MeterView extends LinearLayout {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/diabetes";
    private ImageView ivIcon;
    private TextView tvTitle;
    private int titleVisible;
    private TextView tvValue;
    private CircleProgressBar progressBar;

    public MeterView(Context context) {
        super(context);
        initAtrr(context,null);
        initView();
    }

    public MeterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtrr(context,attrs);
        initView();
    }

    public MeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtrr(context,attrs);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MeterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtrr(context,attrs);
        initView();
    }

    private void initAtrr(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MeterView);
        titleVisible = a.getInt(R.styleable.MeterView_title_visible,VISIBLE);
        a.recycle();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_meter, this);
        ivIcon = (ImageView) findViewById(R.id.icon);
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
        tvValue.setText(String.valueOf(value));
        progressBar.setProgress(value);
    }
}
