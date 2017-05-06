package com.czk.diabetes.medicine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.net.SearchType;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.TagCloud.FlowLayout;
import com.czk.diabetes.view.TagCloud.TagAdapter;
import com.czk.diabetes.view.TagCloud.TagFlowLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by xuezaishao on 2017/3/28.
 */

public class SearchMedicineActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private final static int SEARCH_KEY_FINSH = 2;
    private ImageView ivIcon;
    private ImageView ivCancel;
    private EditText etSearch;
    private List<MedicineData> hots = new ArrayList<>();
    private List<MedicineData> historys = new ArrayList<>();
    private List<MedicineData> keyResults = new ArrayList<>();
    private View viewHotAndHistory;
    private ListView listViewResult;
    private TextView tvClearAll;
    private TagFlowLayout historyLayout;
    private TagFlowLayout hotLayout;
    private MTagAdapter historyAdapter;
    private MTagAdapter hotAdapter;
    private ResultsAdapter keyResultsAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    historyAdapter.notifyDataChanged();
                    hotAdapter.notifyDataChanged();
                    break;
                case SEARCH_KEY_FINSH:
                    viewHotAndHistory.setVisibility(View.GONE);
                    listViewResult.setVisibility(View.VISIBLE);
                    keyResultsAdapter.notifyDataSetChanged();
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(SearchMedicineActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    private void analyticJSON(JSONObject obj, SearchType type) {
        if (obj != null) {
            try {
                if (SearchType.MEDICINE_HOT_AND_HISTORY == type) {
                    JSONObject jsonObject = obj.getJSONObject("body");
                    String resultMsg = jsonObject.getString("msg");
                    if (resultMsg.equals("操作数据库成功")) {
                        JSONArray historyArray = jsonObject.getJSONObject("obj").getJSONArray("historyModels");
                        for (int i = 0; i < historyArray.length(); i++) {
                            MedicineData medicineData = new MedicineData(
                                    historyArray.getJSONObject(i).getString("businessName")
                                    , historyArray.getJSONObject(i).getString("manufacturer")
                                    , historyArray.getJSONObject(i).getString("searchDrugId")
                                    , historyArray.getJSONObject(i).toString());
                            historys.add(medicineData);

                        }
                        JSONArray hotArray = jsonObject.getJSONObject("obj").getJSONArray("hotModels");
                        for (int i = 0; i < hotArray.length(); i++) {
                            MedicineData medicineData = new MedicineData(
                                    hotArray.getJSONObject(i).getString("businessName")
                                    , hotArray.getJSONObject(i).getString("manufacturer")
                                    , hotArray.getJSONObject(i).getString("searchDrugId")
                                    , hotArray.getJSONObject(i).toString());
                            hots.add(medicineData);
                        }
                    } else {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                } else if (SearchType.MEDICINE_KEY_WORD == type) {
                    JSONObject jsonObject = obj.getJSONObject("body");
                    String resultMsg = jsonObject.getString("msg");
                    if (resultMsg.equals("操作数据库成功")) {
                        JSONArray keyArray = jsonObject.getJSONArray("rows");
                        for (int i = 0; i < keyArray.length(); i++) {
                            MedicineData medicineData = new MedicineData(
                                    keyArray.getJSONObject(i).getString("businessName")
                                    , keyArray.getJSONObject(i).getString("manufacturer")
                                    , keyArray.getJSONObject(i).getString("searchDrugId")
                                    , keyArray.getJSONObject(i).toString());
                            keyResults.add(medicineData);
                        }
                    } else {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(SEARCH_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_medicine);
        initData();
        initView();
        dealEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDatas();

    }

    private void initAsnData() {
        DiabetesClient.post(DiabetesClient.getZKTAbsoluteUrl("getHistoryAndHotDrugs")
                , DiabetesClient.getHistoryAndHotDrugsParams()
                , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                , SearchType.MEDICINE_HOT_AND_HISTORY
                                , new SearchThread.OnSearchResult() {
                            @Override
                            public void searchResult(JSONObject jsonObject, Object type) {
                                try {
                                    analyticJSON(jsonObject, (SearchType) type);
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

    private void initData() {
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        ivCancel = (ImageView) findViewById(R.id.search_cancel_icon);
        FontIconDrawable iconCross = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_cross);
        iconCross.setTextColor(getResources().getColor(R.color.white));
        ivCancel.setImageDrawable(iconCross);
        etSearch = (EditText) findViewById(R.id.search_etxt);
        /**
         * 主体
         */
        //热点和历史记录
        viewHotAndHistory = findViewById(R.id.hot_and_history);
        tvClearAll = (TextView) findViewById(R.id.clear_all);
        historyLayout = (TagFlowLayout) findViewById(R.id.history_layout);
        historyAdapter = new MTagAdapter(historys, historyLayout);
        historyLayout.setAdapter(historyAdapter);

        hotLayout = (TagFlowLayout) findViewById(R.id.hot_layout);
        hotAdapter = new MTagAdapter(hots, hotLayout);
        hotLayout.setAdapter(hotAdapter);

        //查询结果
        listViewResult = (ListView) findViewById(R.id.serch_result);
        keyResultsAdapter = new ResultsAdapter(keyResults, this);
        listViewResult.setAdapter(keyResultsAdapter);


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    ivCancel.setVisibility(View.GONE);
                } else {
                    ivCancel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    DiabetesClient.post(DiabetesClient.getZKTAbsoluteUrl("getDrugsList")
                            , DiabetesClient.getDrugsListParams(v.getText().toString())
                            , new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                            , SearchType.MEDICINE_KEY_WORD
                                            , new SearchThread.OnSearchResult() {
                                        @Override
                                        public void searchResult(JSONObject jsonObject, Object type) {
                                            try {
                                                keyResults.clear();
                                                analyticJSON(jsonObject, (SearchType) type);
                                                handler.sendEmptyMessage(SEARCH_KEY_FINSH);
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
                    return true;
                }
                return false;
            }
        });

        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiabetesClient.post(DiabetesClient.getZKTAbsoluteUrl("delDrugsSearch")
                        , DiabetesClient.delDrugsSearchParams()
                        , new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                refreshDatas();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                handler.sendEmptyMessage(SEARCH_ERRO);
                            }
                        });
            }
        });

        historyLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                startMedicineActivity(historys.get(position));
                return true;
            }
        });

        hotLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                startMedicineActivity(hots.get(position));
                return true;
            }
        });

        listViewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMedicineActivity(keyResults.get(position));
            }
        });
    }

    private void startMedicineActivity(MedicineData medicineData) {
        Intent intent = new Intent(SearchMedicineActivity.this, MedicineInfoActivity.class);
        intent.putExtra("obj", medicineData.obj);
        startActivity(intent);
        DiabetesClient.post(DiabetesClient.getZKTAbsoluteUrl("addDrugsSearch")
                , DiabetesClient.addDrugsSearchParams(medicineData.searchDrugId)
                , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                });
    }

    private void refreshDatas() {
        //清空之前数据，重新重新进行网络请求
        hots.clear();
        historys.clear();
        initAsnData();
    }

    private class MTagAdapter<Object> extends TagAdapter<Object> {
        private final FlowLayout flowLayout;

        public MTagAdapter(List datas, FlowLayout flowLayout) {
            super(datas);
            this.flowLayout = flowLayout;
        }


        @Override
        public View getView(FlowLayout parent, int position, java.lang.Object s) {
            LayoutInflater mInflater = LayoutInflater.from(SearchMedicineActivity.this);
            TextView tv = (TextView) mInflater.inflate(R.layout.tag_layout,
                    flowLayout, false);
            tv.setText(((MedicineData) s).businessName);
            return tv;
        }
    }

    private class MedicineData {
        private String businessName;
        private String obj;
        public String searchDrugId;
        public String manufacturer;

        public MedicineData(String businessName, String manufacturer, String searchDrugId, String obj) {
            this.businessName = businessName;
            this.manufacturer = manufacturer;
            this.obj = obj;
            this.searchDrugId = searchDrugId;
        }
    }

    private class ResultsAdapter extends BaseAdapter {
        private List<MedicineData> keyResults;
        private Context context;

        public ResultsAdapter(List<MedicineData> keyResults, Context context) {
            this.keyResults = keyResults;
            this.context = context;
        }

        @Override
        public int getCount() {
            return keyResults.size();
        }

        @Override
        public Object getItem(int position) {
            return keyResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            MedicineData medicineData = (MedicineData) getItem(position);
            if (null == convertView) {
                LayoutInflater inflator = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(R.layout.item_medicine_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.tvManufacturer = (TextView) convertView.findViewById(R.id.manufacturer);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvName.setText(medicineData.businessName);
            viewHolder.tvManufacturer.setText(medicineData.manufacturer);
            return convertView;
        }

        private class ViewHolder {
            private TextView tvName;
            private TextView tvManufacturer;
        }
    }
}
