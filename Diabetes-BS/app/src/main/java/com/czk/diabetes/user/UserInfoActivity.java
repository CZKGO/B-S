package com.czk.diabetes.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 陈忠凯 on 2017/5/31.
 */

public class UserInfoActivity extends BaseActivity {
    private final static int SEARCH_ERRO = 2;
    private final static int SEARCH_FINSH = 3;
    private ImageView ivIcon;
    private ImageView userIV;
    private TextView userTv;
    private TextView tvAge;
    private TextView tvSex;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.server_time_out));
                    break;
                case SEARCH_FINSH:
                    UserData data = (UserData) msg.obj;
                    Imageloader.getInstance().loadImageByUrl(data.imgUrl
                            , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                            , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                            , R.drawable.default_people
                            , userIV
                            , true);
                    userTv.setText(data.name);
                    tvAge.setText(Html.fromHtml(String.format(getResources().getString(R.string.n_year_old), data.year)));
                    if (0 == data.sex)
                        tvSex.setText(getString(R.string.man));
                    else
                        tvSex.setText(getString(R.string.woman));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initAsnData();
        initView();
        dealEvent();
    }

    private void initAsnData() {
        SearchThread searchThread = null;
        try {
            searchThread = new SearchThread(new JSONObject(MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                    .getString(SharedPreferencesUtils.USER_INFO, "null"))
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
        } catch (JSONException e) {


        }
        searchThread.start();
    }

    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.user_info));
        /**主体**/
        userIV = (ImageView) findViewById(R.id.user_icon);
        userTv = (TextView) findViewById(R.id.user_tv);
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvSex = (TextView) findViewById(R.id.tv_sex);
    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.bt_log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance()
                        .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .commit();
                Intent intent = new Intent(UserInfoActivity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(LogInActivity.INTENT_SHOW_SPLASH, false);
                startActivity(intent);
                finish();
            }
        });
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                int code = obj.getInt("code");
                if (0 == code) {
                    JSONArray usersObj = obj.getJSONArray("obj");
                    if (null != usersObj && usersObj.length() > 0) {
                        JSONObject userObj = usersObj.getJSONObject(0);
                        MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                                .edit()
                                .putString(SharedPreferencesUtils.USER_INFO, obj.toString())
                                .apply();
                        UserData doctorData = new UserData(
                                userObj.getString("name")
                                , userObj.getString("pwd")
                                , userObj.getInt("sex")
                                , userObj.getInt("year")
                                , userObj.getString("img"));
                        Message msg = new Message();
                        msg.obj = doctorData;
                        msg.what = SEARCH_FINSH;
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                } else {
                    handler.sendEmptyMessage(SEARCH_ERRO);
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(SEARCH_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }
}
