package com.czk.diabetes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.czk.diabetes.DB.DBOpenHelper;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.user.UserData;
import com.czk.diabetes.user.UserInfoActivity;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.MeterView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class MainFragment extends Fragment {

    private final static int SELECT_DATA_FROM_DB = 0;
    private final static int GET_TITLE_FINISH = 1;
    private final static int SEARCH_ERRO = 2;
    private final static int SEARCH_FINSH = 3;
    String url;
    private View fragment;
    private ImageView userIV;
    private TextView userTv;
    private long currentTime;
    private MeterView meterOne;
    private MeterView meterTow;
    private List<String> timeSlots;
    private TextView dailyReadTV;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SELECT_DATA_FROM_DB:
                    float values[] = (float[]) msg.obj;
                    if (values.length > 0)
                        meterOne.setValue(values[0]);
                    if (values.length > 1)
                        meterTow.setValue(values[1]);
                    break;
                case GET_TITLE_FINISH:
                    dailyReadTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.daily_reading), msg.obj)));
                    break;
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
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_mainpage, container, false);
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
        timeSlots = Arrays.asList(getResources().getStringArray(R.array.time_slots));
        url = "http://holdok.com/pageinfo/tnb/"
                + Long.parseLong(TimeUtil.getYearMonthDay(System.currentTimeMillis()).replace("-", "")) % 1152;
    }

    private void initAsnData() {
        NewsThread newsThread = new NewsThread();
        newsThread.start();
        if (!MyApplication.getInstance().getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                .contains(SharedPreferencesUtils.USER_INFO)) {
            String sql = "SELECT * FROM `users` WHERE name='" + MyApplication.getInstance()
                    .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                    .getString(SharedPreferencesUtils.USER_NAME, "") + "'";
            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("doSql")
                    , DiabetesClient.doSql(sql)
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
                searchThread.start();
            } catch (JSONException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(SEARCH_ERRO);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //输入血糖
        currentTime = System.currentTimeMillis();
        setCircularTile(meterOne, meterTow, TimeUtil.getHourOfTheDay(System.currentTimeMillis()));
        setValue();
    }

    private void setValue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(getActivity());
                SQLiteDatabase db = helper.getWritableDatabase();
                float values[] = new float[2];
                Cursor c = db.query("blood_sugar_record"
                        , new String[]{"time_slot", "value"}
                        , "date=?"
                        , new String[]{TimeUtil.getYearMonthDay(currentTime)}
                        , null, null, null);
                while (c.moveToNext()) {
                    int timeSlot = c.getInt(c.getColumnIndex("time_slot"));
                    if (timeSlot == timeSlots.indexOf(meterOne.getTitle()))
                        values[0] = c.getFloat(c.getColumnIndex("value"));
                    if (timeSlot == timeSlots.indexOf(meterTow.getTitle()))
                        values[1] = c.getFloat(c.getColumnIndex("value"));
                }
                Message message = new Message();
                message.obj = values;
                message.what = SELECT_DATA_FROM_DB;
                handler.sendMessage(message);
                c.close();
                db.close();
            }
        }).start();

    }

    private void setCircularTile(MeterView oneTile, MeterView towTile, int hourOfTheDay) {
        if (0 <= hourOfTheDay && hourOfTheDay < 6) {
            oneTile.setTile(getResources().getString(R.string.before_dawn));
            towTile.setTile(getResources().getString(R.string.before_breakfast));
        } else if (6 <= hourOfTheDay && hourOfTheDay < 8) {
            oneTile.setTile(getResources().getString(R.string.before_breakfast));
            towTile.setTile(getResources().getString(R.string.after_breakfast));
        } else if (8 <= hourOfTheDay && hourOfTheDay < 11) {
            oneTile.setTile(getResources().getString(R.string.after_breakfast));
            towTile.setTile(getResources().getString(R.string.before_lunch));
        } else if (11 <= hourOfTheDay && hourOfTheDay < 15) {
            oneTile.setTile(getResources().getString(R.string.before_lunch));
            towTile.setTile(getResources().getString(R.string.after_lunch));
        } else if (15 <= hourOfTheDay && hourOfTheDay < 17) {
            oneTile.setTile(getResources().getString(R.string.after_lunch));
            towTile.setTile(getResources().getString(R.string.before_dinner));
        } else if (17 <= hourOfTheDay && hourOfTheDay < 22) {
            oneTile.setTile(getResources().getString(R.string.before_dinner));
            towTile.setTile(getResources().getString(R.string.after_dinner));
        } else {
            oneTile.setTile(getResources().getString(R.string.after_dinner));
            towTile.setTile(getResources().getString(R.string.before_sleep));
        }
    }

    private void initView() {
        //登录卡片
        userIV = (ImageView) fragment.findViewById(R.id.user_icon);
        userTv = (TextView) fragment.findViewById(R.id.user_tv);
        ImageView chevronRightIV = (ImageView) fragment.findViewById(R.id.img_sig_chevron_right);
        FontIconDrawable chevronRightDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_chevron_thin_right);
        chevronRightDrawable.setTextColor(ThemeUtil.getThemeColorLight());
        chevronRightIV.setImageDrawable(chevronRightDrawable);
        //每日一读
        ImageView cozyTipIcon = (ImageView) fragment.findViewById(R.id.cozy_tip_icon);
        FontIconDrawable cozyTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_heart);
        cozyTipIconfontDrawable.setTextColor(getResources().getColor(R.color.background_white));
        cozyTipIcon.setImageDrawable(cozyTipIconfontDrawable);
        ImageView readTipIcon = (ImageView) fragment.findViewById(R.id.read_tip_icon);
        FontIconDrawable readTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_book);
        readTipIconfontDrawable.setTextColor(getResources().getColor(R.color.background_white));
        readTipIcon.setImageDrawable(readTipIconfontDrawable);
        dailyReadTV = (TextView) fragment.findViewById(R.id.tv_daily_reading);
        dailyReadTV.setText(Html.fromHtml(String.format(getResources().getString(R.string.daily_reading), getResources().getString(R.string.loading))));


        //输入血糖
        /*标题*/
        ImageView bloodTipIcon = (ImageView) fragment.findViewById(R.id.blood_tip_icon);
        FontIconDrawable bloodTipIconfontDrawable = FontIconDrawable.inflate(getActivity(), R.xml.icon_droplet);
        bloodTipIconfontDrawable.setTextColor(getResources().getColor(R.color.background_white));
        bloodTipIcon.setImageDrawable(bloodTipIconfontDrawable);
        chevronRightIV = (ImageView) fragment.findViewById(R.id.img_blood_chevron_right);
        chevronRightIV.setImageDrawable(chevronRightDrawable);

        /*表盘*/
        meterOne = (MeterView) fragment.findViewById(R.id.meter_one);
        meterTow = (MeterView) fragment.findViewById(R.id.meter_tow);
        bloodTipIconfontDrawable.setTextColor(getResources().getColor(R.color.background_white_light));
        meterOne.setIcon(bloodTipIconfontDrawable);
        meterTow.setIcon(bloodTipIconfontDrawable);
    }

    private void dealEvent() {
        //登录卡片
        View signCard = fragment.findViewById(R.id.sign_card);
        signCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
            }
        });
        //每日一读
        fragment.findViewById(R.id.daily_reading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra("title", dailyReadTV.getText().toString());
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        //输入血糖
        meterOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddValueActivity.class);
                intent.putExtra("currentTime", currentTime);
                intent.putExtra("timeslot", meterOne.getTitle());
                intent.putExtra("value", meterOne.getValue());
                startActivity(intent);
            }
        });
        meterTow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddValueActivity.class);
                intent.putExtra("currentTime", currentTime);
                intent.putExtra("timeslot", meterTow.getTitle());
                intent.putExtra("value", meterTow.getValue());
                startActivity(intent);
            }
        });
        fragment.findViewById(R.id.blood_sugar_card_title)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddValueActivity.class);
                        intent.putExtra("currentTime", currentTime);
                        intent.putExtra("timeslot", meterTow.getTitle());
                        intent.putExtra("value", meterTow.getValue());
                        startActivity(intent);
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

    private class NewsThread extends Thread {
        @Override
        public void run() {
            try {
                Document doc = Jsoup.connect(url).timeout(10000).get();
                Elements titleElement = doc.getElementsByAttributeValue("class", "title");
                Message message = new Message();
                message.obj = titleElement.text();
                message.what = GET_TITLE_FINISH;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
