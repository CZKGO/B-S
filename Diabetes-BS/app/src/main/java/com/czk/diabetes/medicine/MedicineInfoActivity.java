package com.czk.diabetes.medicine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 陈忠凯 on 2017/3/29.
 */

public class MedicineInfoActivity extends BaseActivity {
    private final static int ANALYTIC_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private String obj;
    private ImageView ivIcon;
    private TextView tvBusinessName;
    private TextView tvDrugName;
    private TextView tvDrugUsage;
    private TextView tvInsertDt;
    private TextView tvIsValid;
    private TextView tvManufacturer;
    private TextView tvMemberId;
    private TextView tvModifyDt;
    private TextView tvSearchDrugId;
    private TextView tvSearchId;
    private TextView tvSideEffect;
    private TextView tvAdverseReactions;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ANALYTIC_FINSH:
                    if (msg.obj != null) {
                        MedicineData medicineData = (MedicineData) msg.obj;
                        tvBusinessName.setText(medicineData.businessName);
                        tvDrugName.setText(medicineData.drugName);
                        tvDrugUsage.setText(medicineData.drugUsage);
                        tvManufacturer.setText(medicineData.manufacturer);
                        tvSideEffect.setText(medicineData.sideEffect);
                        tvAdverseReactions.setText(medicineData.adverseReactions);
                    }
                    break;
                case SEARCH_ERRO:
                    ToastUtil.showShortToast(MedicineInfoActivity.this, getResources().getString(R.string.server_time_out));
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
        obj = getIntent().getStringExtra("obj");
    }

    private void initAsnData() {
        AnalyticThread analyticThread = new AnalyticThread(obj);
        analyticThread.start();
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
        tvBusinessName = (TextView) findViewById(R.id.tv_businessName);
        tvDrugName = (TextView) findViewById(R.id.tv_drugName);
        tvDrugUsage = (TextView) findViewById(R.id.tv_drugUsage);
        tvManufacturer = (TextView) findViewById(R.id.tv_manufacturer);
        tvSideEffect = (TextView) findViewById(R.id.tv_sideEffect);
        tvAdverseReactions = (TextView) findViewById(R.id.tv_adverseReactions);


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
            try {
                MedicineData medicineData = new MedicineData();
                medicineData.businessName = obj.getString("businessName");
                medicineData.drugName = obj.getString("drugName");
                medicineData.drugUsage = obj.getString("drugUsage");
                medicineData.insertDt = obj.getString("insertDt");
                medicineData.isValid = obj.getString("isValid");
                medicineData.manufacturer = obj.getString("manufacturer");
                medicineData.memberId = obj.getString("memberId");
                medicineData.modifyDt = obj.getString("modifyDt");
                medicineData.searchDrugId = obj.getString("searchDrugId");
                medicineData.searchId = obj.getString("searchId");
                medicineData.sideEffect = obj.getString("sideEffect");
                medicineData.adverseReactions = obj.getString("adverseReactions");
                Message message = new Message();
                message.what = ANALYTIC_FINSH;
                message.obj = medicineData;
                handler.sendMessage(message);

            } catch (JSONException e) {
                handler.sendEmptyMessage(SEARCH_ERRO);
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
                handler.sendEmptyMessage(ANALYTIC_FINSH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MedicineData {
        private String businessName;
        private String drugName;
        private String drugUsage;
        private String insertDt;
        private String isValid;
        private String manufacturer;
        private String memberId;
        private String modifyDt;
        private String searchDrugId;
        private String searchId;
        private String sideEffect;
        private String adverseReactions;
    }
}
