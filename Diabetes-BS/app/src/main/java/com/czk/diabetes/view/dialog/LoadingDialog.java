package com.czk.diabetes.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.czk.diabetes.R;
import com.czk.diabetes.view.loading.LoadingView;
import com.czk.diabetes.view.loading.render.extend.WaterBottleLoadingRenderer;

/**
 * Created by 陈忠凯 on 2017/5/4.
 */

public class LoadingDialog extends Dialog {


    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialo_loading);

        LoadingView loadingView= (LoadingView) findViewById(R.id.loading_view);
        WaterBottleLoadingRenderer renderer = new WaterBottleLoadingRenderer.Builder(getContext())
                .build();
        loadingView.setLoadingRenderer(renderer);
    }

    @Override
    public void onBackPressed() {
    }
}
