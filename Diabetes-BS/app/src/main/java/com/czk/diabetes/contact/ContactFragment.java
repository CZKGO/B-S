package com.czk.diabetes.contact;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class ContactFragment extends Fragment {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private final static int SEARCH_CONTACT_FINSH = 3;
    private final static int SEARCH_CONTACT_ERRO = 4;
    private final static int SEND_CONTACT_SUCCESS = 5;
    private final static int SEND_CONTACT_ERRO = 6;
    private final static int ID = 1;//默认医生
    private final static String TAG = "ContactFragment";//默认医生
    private View fragment;
    private ImageView doctorIV;
    private TextView nameTv;
    private TextView hospitalTV;
    private TextView positionTV;
    private TextView feedbackTV;
    private TextView yearsTV;
    private TextView tvNoContact;
    private ListView lvContact;
    private List<ContactData> contactList;
    private ContactAdapter adpter;
    private EditText etContent;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    DoctorData data = (DoctorData) msg.obj;
                    Imageloader.getInstance().loadImageByUrl(data.imgUrl
                            , DimensUtil.dpTopx(MyApplication.getInstance(), 80)
                            , DimensUtil.dpTopx(MyApplication.getInstance(), 80)
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
                case SEARCH_CONTACT_FINSH:
                    tvNoContact.setVisibility(View.GONE);
                    lvContact.setVisibility(View.VISIBLE);
                    adpter.notifyDataSetChanged();
                    break;
                case SEARCH_CONTACT_ERRO:
                    tvNoContact.setVisibility(View.VISIBLE);
                    lvContact.setVisibility(View.GONE);
                    break;
                case SEND_CONTACT_SUCCESS:
                    refreshContactList(0);
                    etContent.setText("");
                    break;
                case SEND_CONTACT_ERRO:
                    tvNoContact.setVisibility(View.VISIBLE);
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.send_fail));
                    break;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_contact, container, false);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initAsnData();
        dealEvent();
    }

    private void initData() {
        contactList = new ArrayList<>();
        adpter = new ContactAdapter(contactList);
    }

    private void dealEvent() {
        fragment.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(etContent.getText().toString())) {
                    if (null != getActivity()) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                        }
                    }
                    final String name = MyApplication.getInstance()
                            .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                            .getString(SharedPreferencesUtils.USER_NAME, "");
                    DiabetesClient.get(DiabetesClient.getAbsoluteUrl("creatContactTable")
                            , DiabetesClient.creatContactTable(name)
                            , new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    DiabetesClient.get(DiabetesClient.getAbsoluteUrl("sendContactInfo")
                                            , DiabetesClient.sendContactInfo(name, etContent.getText().toString(), System.currentTimeMillis(), ID)
                                            , new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                                            , null
                                                            , new SearchThread.OnSearchResult() {
                                                        @Override
                                                        public void searchResult(JSONObject jsonObject, Object type) {
                                                            try {
                                                                analyticSendJSON(jsonObject);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void error() {
                                                            handler.sendEmptyMessage(SEND_CONTACT_ERRO);
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

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    handler.sendEmptyMessage(SEARCH_ERRO);
                                }
                            });
                } else {
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.cant_null));
                }
            }
        });
    }


    private void initView() {
        doctorIV = (ImageView) fragment.findViewById(R.id.iv_doctor);
        nameTv = (TextView) fragment.findViewById(R.id.txt_name);
        hospitalTV = (TextView) fragment.findViewById(R.id.txt_hospital);
        positionTV = (TextView) fragment.findViewById(R.id.txt_position);
        feedbackTV = (TextView) fragment.findViewById(R.id.txt_feedback);
        yearsTV = (TextView) fragment.findViewById(R.id.txt_years);

        tvNoContact = (TextView) fragment.findViewById(R.id.tv_contact_list);
        lvContact = (ListView) fragment.findViewById(R.id.list);
        lvContact.setAdapter(adpter);

        etContent = (EditText) fragment.findViewById(R.id.et_contet);
    }

    private void initAsnData() {
        if (!MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .contains(SharedPreferencesUtils.DOCTOR_INFO)) {
            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("getDoctor")
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
                        .getString(SharedPreferencesUtils.DOCTOR_INFO, "null"))
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
        refreshContactList(1);
    }

    private void refreshContactList(final int type) {
        final String name = MyApplication.getInstance()
                .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .getString(SharedPreferencesUtils.USER_NAME, "");
        DiabetesClient.get(DiabetesClient.getAbsoluteUrl("getContectList")
                , DiabetesClient.getContectList(name, ID)
                , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                , null
                                , new SearchThread.OnSearchResult() {
                            @Override
                            public void searchResult(JSONObject jsonObject, Object otype) {
                                try {
                                    analyticContactJSON(jsonObject, name, type);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void error() {
                                if (type == 1)
                                    handler.sendEmptyMessage(SEARCH_CONTACT_ERRO);
                                else if (type == 0)
                                    handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                            }
                        });
                        searchThread.start();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        if (type == 1)
                            handler.sendEmptyMessage(SEARCH_CONTACT_ERRO);
                        else if (type == 0)
                            handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                    }
                });
    }

    private void analyticContactJSON(JSONObject obj, String name, int type) {
        if (obj != null) {
            JSONArray keyArray = null;
            try {
                keyArray = obj.getJSONArray("obj");
                contactList.clear();
                for (int i = 0; i < keyArray.length(); i++) {
                    ContactData data = new ContactData(keyArray.getJSONObject(i).getInt("type")
                            , keyArray.getJSONObject(i).getString("text")
                            , keyArray.getJSONObject(i).getString("time")
                            , keyArray.getJSONObject(i).getString("doctor")
                            , keyArray.getJSONObject(i).getString("ui")
                            , keyArray.getJSONObject(i).getString("di")
                            , name
                            , keyArray.getJSONObject(i).getString("name"));
                    contactList.add(data);
                }
                handler.sendEmptyMessage(SEARCH_CONTACT_FINSH);
            } catch (JSONException e) {
                if (type == 1)
                    handler.sendEmptyMessage(SEARCH_CONTACT_ERRO);
                else if (type == 0)
                    handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                e.printStackTrace();
            }
        } else {
            if (type == 1)
                handler.sendEmptyMessage(SEARCH_CONTACT_ERRO);
            else if (type == 0)
                handler.sendEmptyMessage(SEND_CONTACT_ERRO);
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
                            , obj.getString("img"));
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


    private void analyticSendJSON(JSONObject obj) {
        if (obj != null) {
            try {
                int code = obj.getInt("code");
                if (0 == code) {
                    handler.sendEmptyMessage(SEND_CONTACT_SUCCESS);
                } else if (1 == code) {
                    handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                } else {
                    handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(SEND_CONTACT_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(SEND_CONTACT_ERRO);
        }
    }
}
