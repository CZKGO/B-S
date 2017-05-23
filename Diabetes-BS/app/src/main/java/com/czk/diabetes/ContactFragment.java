package com.czk.diabetes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class ContactFragment extends Fragment {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private final static int ID = 1;//默认医生
    private View fragment;
    private ImageView doctorIV;
    private TextView nameTv;
    private TextView hospitalTV;
    private TextView positionTV;
    private TextView feedbackTV;
    private TextView yearsTV;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    DoctorData data = (DoctorData) msg.obj;
                    Imageloader.getInstance().loadImageByUrl(data.imgUrl
                            , DimensUtil.dpTopx(MyApplication.getInstance(),80)
                            , DimensUtil.dpTopx(MyApplication.getInstance(),80)
                            , R.drawable.default_people
                            , doctorIV
                            , true);
                    nameTv.setText(Html.fromHtml(String.format(getResources().getString(R.string.name), data.name)));
                    hospitalTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.hospital), data.hospital)));
                    positionTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.position), data.position)));
                    feedbackTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.feedback), data.feedback)));
                    yearsTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.years), data.years)));
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_contact, container, false);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initAsnData();
        dealEvent();
    }

    private void dealEvent() {

    }

    private void initView() {
        doctorIV = (ImageView) fragment.findViewById(R.id.iv_doctor);
        nameTv = (TextView) fragment.findViewById(R.id.txt_name);
        hospitalTV = (TextView) fragment.findViewById(R.id.txt_hospital);
        positionTV = (TextView) fragment.findViewById(R.id.txt_position);
        feedbackTV = (TextView) fragment.findViewById(R.id.txt_feedback);
        yearsTV = (TextView) fragment.findViewById(R.id.txt_years);
    }

    private void initAsnData() {
        if (!MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .contains(SharedPreferencesUtils.DOCTOR_INFO)) {
            DiabetesClient.post(DiabetesClient.getAbsoluteUrl("getDoctor")
                    , DiabetesClient.getDoctor(ID)
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                    , null
                                    , new SearchThread.OnSearchResult() {
                                @Override
                                public void searchResult(JSONObject jsonObject, Object type) {
                                    try {
                                        analyticJSON(jsonObject);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void error() {
                                    handler.sendEmptyMessage(SEARCH_ERRO);
                                }
                            });
                            searchThread.start();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            handler.sendEmptyMessage(SEARCH_ERRO);
                        }
                    });
        } else {
            try {
                SearchThread searchThread = new SearchThread(new JSONObject(MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                        .getString(SharedPreferencesUtils.DOCTOR_INFO,"null"))
                        , null
                        , new SearchThread.OnSearchResult() {
                    @Override
                    public void searchResult(JSONObject jsonObject, Object type) {
                        try {
                            analyticJSON(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void error() {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                });
                searchThread.start();
            } catch (JSONException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(SEARCH_ERRO);
            }
        }
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                int id = obj.getInt("id");
                if (ID != id) {
                    handler.sendEmptyMessage(SEARCH_ERRO);
                } else {
                    MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                            .edit()
                            .putString(SharedPreferencesUtils.DOCTOR_INFO, obj.toString())
                            .apply();
                    DoctorData doctorData = new DoctorData(
                            id
                            , obj.getString("name")
                            , obj.getString("hospital")
                            , obj.getString("position")
                            , obj.getString("years")
                            , obj.getString("feedback")
                            , obj.getString("imgUrl"));
                    Message msg = new Message();
                    msg.obj = doctorData;
                    msg.what = SEARCH_FINSH;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(SEARCH_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }

    private class DoctorData {
        private final String position;
        public int id;
        public String name;
        public String hospital;
        public String years;
        public String imgUrl;
        public String feedback;

        public DoctorData(int id, String name, String hospital, String position, String years, String feedback, String img) {
            this.id = id;
            this.name = name;
            this.hospital = hospital;
            this.years = years;
            this.feedback = feedback;
            this.imgUrl = img;
            this.position = position;
        }
    }
}
