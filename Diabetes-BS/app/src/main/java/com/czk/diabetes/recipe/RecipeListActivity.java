package com.czk.diabetes.recipe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.net.SearchType;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/29.
 */

public class RecipeListActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private ImageView ivIcon;
    private LinkedList<RecipeData> cookBooks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    if (msg.obj != null) {
                    }
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(RecipeListActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_info);
        initData();
        initAsnData();
        initView();
        dealEvent();
    }


    private void initData() {

    }

    private void initAsnData() {
        DiabetesClient.post(DiabetesClient.getZKTRECIPEAbsoluteUrl("getCookBooksNew")
                , DiabetesClient.getCookBooksNew()
                , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                , SearchType.RECIPE_LIST
                                , new SearchThread.OnSearchResult() {
                            @Override
                            public void searchResult(JSONObject jsonObject, Object type) {
                                try {
                                    analyticJSON(jsonObject);
                                    handler.sendEmptyMessage(SEARCH_FINSH);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        searchThread.start();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                });
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.medicine_info));
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

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            JSONArray keyArray = null;
            try {
                keyArray = obj.getJSONArray("rows");

                for (int i = 0; i < keyArray.length(); i++) {
                    RecipeData medicineData = new RecipeData(
                            keyArray.getJSONObject(i).getString("cookbookName")
                            , keyArray.getJSONObject(i).getString("imgUrl")
                            , keyArray.getJSONObject(i).toString());
                    cookBooks.add(medicineData);
                }
            } catch (JSONException e) {
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
//                handler.sendEmptyMessage(ANALYTIC_FINSH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RecipeData {
        private String cookbookName;
        private String imgUrl;
        private String obj;

        public RecipeData(String cookbookName, String imgUrl, String obj) {

        }
    }
}
