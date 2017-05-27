package com.czk.diabetes.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.czk.diabetes.R;
import com.czk.diabetes.view.loading.LoadingView;
import com.czk.diabetes.view.loading.render.extend.WaterBottleLoadingRenderer;

/**
 * Created by 陈忠凯 on 2017/5/4.
 */

public class LoadingDialog extends Dialog {
    private String name = "Loading";

    public LoadingDialog(@NonNull Context context, String string) {
        super(context, R.style.MyDialog);
        setCanceledOnTouchOutside(false);
        name = string;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialo_loading);

        TextView tvName = (TextView) findViewById(R.id.tv_name);
        LoadingView loadingView = (LoadingView) findViewById(R.id.loading_view);

        tvName.setText(name);
        WaterBottleLoadingRenderer renderer = new WaterBottleLoadingRenderer.Builder(getContext())
                .build();
        loadingView.setLoadingRenderer(renderer);
    }

    @Override
    public void onBackPressed() {
    }
}
