package com.czk.diabetes.athletic;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.ThemeUtil;
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
 * Created by 陈忠凯 on 2017/3/29.
 */

public class AthleticListActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private final static int LOADING = -1;
    private final static int LOAD_SUCCESS = 0;
    private final static int LOAD_FIALD = 1;
    private ImageView ivIcon;
    private ImageView ivIconRight;
    private List<AthleticData> cookBooks = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ListAdapter adapter;
    private int requestPage = 1;
    private int loadPageSuccess = LOAD_SUCCESS;//0

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    adapter.notifyDataSetChanged();
                    requestPage++;
                    loadPageSuccess = LOAD_SUCCESS;
                    break;
                case SEARCH_ERRO:
                    loadPageSuccess = LOAD_FIALD;
                    ToastUtil.showShortToast(AthleticListActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athletic_list);
        initData();
        initAsnData();
        initView();
        dealEvent();
    }


    private void initData() {
        adapter = new ListAdapter();
    }

    private void initAsnData() {
        getAthleticList();
    }

    private void getAthleticList() {
        if (LOADING != loadPageSuccess) {
            loadPageSuccess = LOADING;
            String sql = "SELECT `athletic`.* , `users`.`img` FROM `athletic` , `users` WHERE `athletic`.`user` = `users`.`name` ORDER BY `athletic`.time DESC ";
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
                                        handler.sendEmptyMessage(SEARCH_FINSH);
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
        }
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
        tvTitle.setText(getResources().getString(R.string.find));
        ivIconRight = (ImageView) findViewById(R.id.icon_right);
        ivIconRight.setVisibility(View.VISIBLE);
        FontIconDrawable iconPlus = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_plus);
        iconPlus.setTextColor(getResources().getColor(R.color.white));
        ivIconRight.setImageDrawable(iconPlus);
        /**
         * 主体
         */
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(AthleticListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceDecoration(DimensUtil.dpTopx(this, 4)));
        recyclerView.setAdapter(adapter);
    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivIconRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AthleticListActivity.this, AddMyAthleticActivity.class));
            }
        });
    }

    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            JSONArray keyArray = null;
            try {
                keyArray = obj.getJSONArray("obj");

                for (int i = 0; i < keyArray.length(); i++) {
                    AthleticData medicineData = new AthleticData(
                            keyArray.getJSONObject(i).getString("user")
                            , keyArray.getJSONObject(i).getString("img")
                            , keyArray.getJSONObject(i).getString("text")
                            , keyArray.getJSONObject(i).getString("imgUrl")
                            , keyArray.getJSONObject(i).getInt("likeNum")
                            , keyArray.getJSONObject(i).getString("likeUsers")
                            , keyArray.getJSONObject(i).getLong("time")
                            , false);
                    cookBooks.add(medicineData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }

    private class AthleticData {
        private String user;
        private String userIconUrl;
        private String text;
        private String imgUrl;
        private int likeNum;
        private String likeUsers;
        public long time;
        public boolean islike;

        public AthleticData(String user, String userIconUrl, String text, String imgUrl, int likeNum, String likeUsers, long time, boolean islike) {
            this.user = user;
            this.userIconUrl = userIconUrl;
            this.text = text;
            this.imgUrl = imgUrl;
            this.likeNum = likeNum;
            this.likeUsers = likeUsers;
            this.time = time;
        }
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
            CardViewHolder holder = new CardViewHolder(LayoutInflater.from(AthleticListActivity.this)
                    .inflate(R.layout.item_athletic_card, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final AthleticData data = cookBooks.get(position);
            if (holder instanceof CardViewHolder) {
                setCardViewColor((CardViewHolder) holder, data.islike);
                ((CardViewHolder) holder).tv.setText(data.user);
                ((CardViewHolder) holder).tvDescribe.setText(data.text);
                ((CardViewHolder) holder).tvLike.setText(String.valueOf(data.likeNum));
                Imageloader.getInstance().loadImageByUrl(data.userIconUrl
                        , DimensUtil.dpTopx(AthleticListActivity.this, 24)
                        , DimensUtil.dpTopx(AthleticListActivity.this, 24)
                        , R.drawable.default_people
                        , ((CardViewHolder) holder).ivHead);
                float cardWidth = (DimensUtil.getScreenWidthDip(AthleticListActivity.this) - 16);
                double cardHight = cardWidth * 0.618;
                ((CardViewHolder) holder).iv.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , (int) cardHight));
                Imageloader.getInstance().loadImageByUrl(data.imgUrl
                        , DimensUtil.dpTopx(AthleticListActivity.this, cardWidth)
                        , DimensUtil.dpTopx(AthleticListActivity.this, (float) cardHight)
                        , R.drawable.img_default
                        , ((CardViewHolder) holder).iv);

                ((CardViewHolder) holder).likeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.islike) {
                            data.likeNum--;
                            data.islike = false;
                        } else {
                            data.likeNum++;
                            data.islike = true;
                        }
                        ((CardViewHolder) holder).tvLike.setText(String.valueOf(data.likeNum));
                        setCardViewColor((CardViewHolder) holder, data.islike);
                    }
                });
            }
        }

        private void setCardViewColor(CardViewHolder holder, Object islike) {
            if (null != islike) {
                FontIconDrawable iconThumbUp = FontIconDrawable.inflate(AthleticListActivity.this, R.xml.icon_thumb_up);
                if ((boolean) islike) {
                    iconThumbUp.setTextColor(ThemeUtil.getThemeColor());
                    holder.tvLike.setTextColor(ThemeUtil.getThemeColor());
                    holder.ivLike.setImageDrawable(iconThumbUp);
                } else {
                    iconThumbUp.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.tvLike.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.ivLike.setImageDrawable(iconThumbUp);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return cookBooks.size();
        }

        private class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;
            ImageView ivHead;
            TextView tvDescribe;
            ImageView ivLike;
            TextView tvLike;
            View likeView;

            public CardViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.img);
                ivHead = (ImageView) itemView.findViewById(R.id.iv_head);
                tvDescribe = (TextView) itemView.findViewById(R.id.tv_describe);
                tv = (TextView) itemView.findViewById(R.id.name);
                ivLike = (ImageView) itemView.findViewById(R.id.like_img);
                tvLike = (TextView) itemView.findViewById(R.id.like_num);
                likeView = itemView.findViewById(R.id.like_click_range);
            }
        }
    }
}
