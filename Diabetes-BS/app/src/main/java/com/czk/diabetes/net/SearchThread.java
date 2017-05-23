package com.czk.diabetes.net;

import com.czk.diabetes.util.StringUtil;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by 陈忠凯 on 2017/4/8.
 */

public class SearchThread extends Thread {
    private JSONObject jsonObject;
    private InputStream inputStream;
    private Object type;
    private OnSearchResult onSearchResult;

    public SearchThread(ByteArrayInputStream inputStream, Object type, OnSearchResult onSearchResult) {
        this.inputStream = inputStream;
        this.type = type;
        this.onSearchResult = onSearchResult;
    }

    public SearchThread(JSONObject jsonObject, Object type, OnSearchResult onSearchResult) {
        this.jsonObject = jsonObject;
        this.type = type;
        this.onSearchResult = onSearchResult;
    }

    @Override
    public void run() {
        try {
            if(null==jsonObject){
                jsonObject = StringUtil.readJsonFromInputStream(inputStream);
            }
            onSearchResult.searchResult(jsonObject,type);
        } catch (Exception e) {
            e.printStackTrace();
            onSearchResult.error();
        }

    }

    public interface OnSearchResult {
        void searchResult(JSONObject jsonObject, Object type);

        void error();
    }
}
