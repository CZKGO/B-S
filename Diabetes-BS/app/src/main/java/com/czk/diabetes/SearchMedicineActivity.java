package com.czk.diabetes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.czk.diabetes.util.ConnectionUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.LogUtil;
import com.czk.diabetes.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xuezaishao on 2017/3/28.
 */

public class SearchMedicineActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private ImageView ivIcon;
    private String title;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:

                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_medicine);
        initData();
        initView();
        initAsnData();
        dealEvent();
    }
    private void initAsnData() {
        SearchThread searchThread = new SearchThread();
        searchThread.start();
    }
    private void initData() {
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(title);
        /**
         * 主体
         */


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private class SearchThread extends Thread {
        @Override
        public void run() {
            try {
                ConnectionUtil.getHistoryAndHotDrugs();
                Message message = new Message();
                message.obj = ConnectionUtil.getHistoryAndHotDrugs();
                message.what = SEARCH_FINSH;
                handler.sendMessage(message);
                LogUtil.d("sdafasfdsadf",StringUtil.convertStreamToString((InputStream) message.obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
