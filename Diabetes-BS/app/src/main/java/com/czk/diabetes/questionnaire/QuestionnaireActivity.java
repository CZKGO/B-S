package com.czk.diabetes.questionnaire;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/6/2.
 */

public class QuestionnaireActivity extends BaseActivity {
    private final static int HANDLER_SEARCH_FINSH = 0;
    private final static int HANDLER_SEARCH_ERRO = 1;
    private final static int HANDLER_NEXT_QUSETION = 2;
    private ImageView ivIcon;
    private RecyclerView recyclerView;
    private TextView tvQuestion;
    private LinearLayoutManager layoutManager;
    private ListAdapter adapter;
    private List<QuestionnaireData> questions;
    private int index = 0;
    private List<QuestionnaireData.AnswerData> answers;
    private int type = QuestionnaireData.TYPE_SINGLE;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SEARCH_FINSH:
                    questions = (List<QuestionnaireData>) msg.obj;
                    if (questions.size() > index) {
                        QuestionnaireData question = questions.get(index);
                        if (null != question.getType()) {
                            type = QuestionnaireData.TYPE_MULTI;
                        }
                        tvQuestion.setText((index + 1) + ". " + question.getQuestion());
                        answers = question.getAnswers();
                        adapter = new ListAdapter();
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShortToast(QuestionnaireActivity.this, getResources().getString(R.string.server_time_out));
                        finish();
                    }
                    break;
                case HANDLER_NEXT_QUSETION:
                    if (questions.size() > index) {
                        QuestionnaireData question = questions.get(index);
                        if (null != question.getType()) {
                            type = QuestionnaireData.TYPE_MULTI;
                        }
                        tvQuestion.setText((index + 1) + ". " + question.getQuestion());
                        answers = question.getAnswers();
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShortToast(QuestionnaireActivity.this, getResources().getString(R.string.survey_completion));
                        finish();
                    }
                    break;
                case HANDLER_SEARCH_ERRO:
                    ToastUtil.showShortToast(QuestionnaireActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        initData();
        initAsnData();
        initView();
        dealEvent();
    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chosed(answers)) {
                    index++;
                    handler.sendEmptyMessage(HANDLER_NEXT_QUSETION);
                }else {
                    ToastUtil.showShortToast(QuestionnaireActivity.this, getResources().getString(R.string.please_choose_answer));
                }
            }
        });
    }

    private boolean chosed(List<QuestionnaireData.AnswerData> answers) {
        for (QuestionnaireData.AnswerData data : answers) {
            if(data.isSelect()){
                return true;
            }
        }
        return false;
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.questionnaire));
        /**
         * 主体
         */
        tvQuestion = (TextView) findViewById(R.id.title_tv);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(QuestionnaireActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceDecoration(DimensUtil.dpTopx(this, 4)));
        recyclerView.setAdapter(adapter);
    }


    private void initData() {
    }

    private void initAsnData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(DiabetesClient.MY_BASE_URL + "wenjuan/questions.xml").openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        //获取服务端的输入流
                        InputStream is = conn.getInputStream();
                        //xml pull解析
                        PullQuestionParser parser = new PullQuestionParser();
                        List<QuestionnaireData> datas = parser.parse(is);
                        Message msg = new Message();
                        msg.what = HANDLER_SEARCH_FINSH;
                        msg.obj = datas;
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(HANDLER_SEARCH_ERRO);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(HANDLER_SEARCH_ERRO);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class SpaceDecoration extends RecyclerView.ItemDecoration {

        private final int space;

        public SpaceDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
        }
    }

    private class ListAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ListAdapter.CardViewHolder holder = new ListAdapter.CardViewHolder(LayoutInflater.from(QuestionnaireActivity.this)
                    .inflate(R.layout.item_answer_card, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final QuestionnaireData.AnswerData data = answers.get(position);
            if (holder instanceof ListAdapter.CardViewHolder) {
                ((ListAdapter.CardViewHolder) holder).tv.setText(data.getCode() + "." + data.getContent());
                setCheckBox(((CardViewHolder) holder).cb, data.isSelect());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == QuestionnaireData.TYPE_SINGLE) {
                            for (QuestionnaireData.AnswerData data : answers) {
                                data.setSelect(false);
                            }
                        }
                        data.setSelect(true);
                        notifyDataSetChanged();
                    }
                });
            }
        }

        private void setCheckBox(CheckBox checkBox, boolean check) {
            if (check) {
                FontIconDrawable iconToggle = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_checkbox_checked);
                iconToggle.setTextSize(DimensUtil.dpTopx(QuestionnaireActivity.this, 16));
                checkBox.setBackgroundDrawable(iconToggle);
                checkBox.setChecked(check);
            } else {
                FontIconDrawable iconToggle = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_checkbox_unchecked);
                iconToggle.setTextSize(DimensUtil.dpTopx(QuestionnaireActivity.this, 16));
                checkBox.setBackgroundDrawable(iconToggle);
                checkBox.setChecked(check);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return answers.size();
        }

        private class CardViewHolder extends RecyclerView.ViewHolder {
            CheckBox cb;
            TextView tv;

            public CardViewHolder(View itemView) {
                super(itemView);
                cb = (CheckBox) itemView.findViewById(R.id.cb_toggle);
                tv = (TextView) itemView.findViewById(R.id.tv_text);
            }
        }
    }

}
