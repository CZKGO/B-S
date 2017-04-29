package com.czk.diabetes.recipe;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 陈忠凯 on 2017/3/29.
 */

public class RecipeInfoActivity extends BaseActivity {
    private final static int ANALYTIC_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private String obj;
    private ImageView ivIcon;
    private TextView tvInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ANALYTIC_FINSH:
                    if (msg.obj != null) {
                        RecipeData data = (RecipeData) msg.obj;
                        tvInfo.setText(Html.fromHtml(data.content, new MyImageGetter(msg.obj), null));
                    }
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(RecipeInfoActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
        initData();
        initAsnData();
        initView();
        dealEvent();
    }


    private void initData() {
        obj = getIntent().getStringExtra("obj");
    }

    private void initAsnData() {
        AnalyticThread analyticThread = new AnalyticThread(obj);
        analyticThread.start();
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.recipe_info));
        /**
         * 主体
         */
        tvInfo = (TextView) findViewById(R.id.info);


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                RecipeData data = new RecipeData();
                data.content = obj.getString("bodyText");
                Message message = new Message();
                message.what = ANALYTIC_FINSH;
                message.obj = data;
                handler.sendMessage(message);

            } catch (JSONException e) {
                handler.sendEmptyMessage(SEARCH_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }

    private class AnalyticThread extends Thread {
        String obj;

        public AnalyticThread(String obj) {
            this.obj = obj;
        }

        @Override
        public void run() {
            try {
                analyticJSON(new JSONObject(obj));
                handler.sendEmptyMessage(ANALYTIC_FINSH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RecipeData {
        private String content;
    }

    private class MyImageGetter implements Html.ImageGetter {
        private Object obj;

        public MyImageGetter(Object obj) {
            this.obj = obj;
        }

        @Override
        public Drawable getDrawable(String source) {
            if (null != source) {
                Drawable drawable = null;
                // 判断是否以http开头
                if (source.startsWith("http")) {
                    return Imageloader.getInstance().getDrawableFromUrl(source, new Imageloader.GetDrawableFromUrlCallBack() {
                        @Override
                        public void onGetEnd() {
                            Message message = new Message();
                            message.what = ANALYTIC_FINSH;
                            message.obj = obj;
                            handler.sendMessage(message);
                        }
                    });
                }
                return drawable;
            }
            return null;
        }
    }
}
