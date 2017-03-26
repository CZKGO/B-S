package com.czk.diabetes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/26.
 */

public class BrowserActivity extends BaseActivity{
    private ImageView ivIcon;
    private String title;
    private String url;
    private WebView webView;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
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
        pb = (ProgressBar) findViewById(R.id.pb);
        webView = (WebView) findViewById(R.id.webview);
        pb.setMax(100);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        MyWebViewClient wvc = new MyWebViewClient();
        webView.setWebViewClient(wvc);
        webView.setWebChromeClient(new ChromeWebViewClient(pb));
        try {
            webView.getSettings().setJavaScriptEnabled(true);
        } catch (Exception e) {

        }
        webView.loadUrl(url);

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class ChromeWebViewClient extends WebChromeClient {
        private ProgressBar pb;

        public ChromeWebViewClient(ProgressBar pb) {
            this.pb = pb;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb.setProgress(newProgress);
            if (newProgress == 100) {
                pb.setProgress(0);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }
    }
}
