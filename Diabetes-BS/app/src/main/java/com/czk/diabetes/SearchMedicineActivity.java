package com.czk.diabetes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.util.ConnectionUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.TagCloud.FlowLayout;
import com.czk.diabetes.view.TagCloud.TagAdapter;
import com.czk.diabetes.view.TagCloud.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xuezaishao on 2017/3/28.
 */

public class SearchMedicineActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private ImageView ivIcon;
    private List<String> hots = new ArrayList<>();
    private List<String> historys = new ArrayList<>();
    private TagFlowLayout historyLayout;
    private TagFlowLayout hotLayout;
    private MTagAdapter historyAdapter;
    private MTagAdapter hotAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    historyAdapter.notifyDataChanged();
                    hotAdapter.notifyDataChanged();
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(SearchMedicineActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                JSONObject jsonObject = obj.getJSONObject("body");
                String resultMsg = jsonObject.getString("msg");
                if (resultMsg.equals("操作数据库成功")) {
                    JSONArray historyArray = jsonObject.getJSONObject("obj").getJSONArray("historyModels");
                    for (int i = 0; i < historyArray.length(); i++) {
                        historys.add(historyArray.getJSONObject(i).getString("businessName"));
                    }
                    JSONArray hotArray = jsonObject.getJSONObject("obj").getJSONArray("hotModels");
                    for (int i = 0; i < hotArray.length(); i++) {
                        hots.add(hotArray.getJSONObject(i).getString("businessName"));
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
        /**
         * 主体
         */
        historyLayout = (TagFlowLayout) findViewById(R.id.history_layout);
        historyAdapter = new MTagAdapter(historys,historyLayout);
        historyLayout.setAdapter(historyAdapter);

        hotLayout = (TagFlowLayout) findViewById(R.id.hot_layout);
        hotAdapter = new MTagAdapter(hots,hotLayout);
        hotLayout.setAdapter(hotAdapter);


    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        historyLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                //Toast.makeText(getActivity(), mVals[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                return true;
            }
        });

        historyLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                setTitle("choose:" + selectPosSet.toString());
            }
        });

        hotLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                //Toast.makeText(getActivity(), mVals[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                return true;
            }
        });

        hotLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                setTitle("choose:" + selectPosSet.toString());
            }
        });
    }

    private class SearchThread extends Thread {
        @Override
        public void run() {
            try {
                analyticJSON(ConnectionUtil.getHistoryAndHotDrugsJSON(SearchMedicineActivity.this));
                handler.sendEmptyMessage(SEARCH_FINSH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            tv.setText(s.toString());
            return tv;
        }
    }
}
