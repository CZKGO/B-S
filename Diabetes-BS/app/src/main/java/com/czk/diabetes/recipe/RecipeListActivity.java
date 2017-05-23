package com.czk.diabetes.recipe;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.czk.diabetes.net.SearchType;
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
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

public class RecipeListActivity extends BaseActivity {
    private final static int SEARCH_FINSH = 0;
    private final static int SEARCH_ERRO = 1;
    private final static int LOADING = -1;
    private final static int LOAD_SUCCESS = 0;
    private final static int LOAD_FIALD = 1;
    private ImageView ivIcon;
    private List<RecipeData> cookBooks = new ArrayList<>();
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private WaterFallAdapter adapter;
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
                    ToastUtil.showShortToast(RecipeListActivity.this, getResources().getString(R.string.server_time_out));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        initData();
        initAsnData();
        initView();
        dealEvent();
    }


    private void initData() {
        adapter = new WaterFallAdapter();
    }

    private void initAsnData() {
        getCookBooks();
    }

    private void getCookBooks() {
        if (LOADING != loadPageSuccess) {
            loadPageSuccess = LOADING;
            DiabetesClient.post(DiabetesClient.getZKTRECIPEAbsoluteUrl("getCookBooksNew")
                    , DiabetesClient.getCookBooksNew(requestPage)
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                    , SearchType.RECIPE_LIST
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
        tvTitle.setText(getResources().getString(R.string.many_recipes));
        /**
         * 主体
         */
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
                //当前RecyclerView显示出来的最后一个的item的position
                int lastPosition = -1;
                //当前状态为停止滑动状态SCROLL_STATE_IDLE时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
                    lastPosition = findMax(lastPositions);


                    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                    //如果相等则说明已经滑动到最后了
                    if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        getCookBooks();
                    }

                }
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
                keyArray = obj.getJSONArray("rows");

                for (int i = 0; i < keyArray.length(); i++) {
                    RecipeData medicineData = new RecipeData(
                            keyArray.getJSONObject(i).getString("cookbookName")
                            , keyArray.getJSONObject(i).getString("imgUrl")
                            , keyArray.getJSONObject(i).optInt("collection", 0) + keyArray.getJSONObject(i).optInt("collectNum")
                            , keyArray.getJSONObject(i).optInt("fabulous", 0) + keyArray.getJSONObject(i).optInt("likeNum", 0)
                            , keyArray.getJSONObject(i).optInt("picWidth", 0)
                            , keyArray.getJSONObject(i).optInt("picHeight", 0)
                            , keyArray.getJSONObject(i).toString());
                    cookBooks.add(medicineData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            handler.sendEmptyMessage(SEARCH_ERRO);
        }
    }

    private class RecipeData {
        private String cookbookName;
        private String imgUrl;
        private int collection;
        private int like;
        private int imgWidth;
        private int imgHight;
        private String obj;
        public boolean isCollect;
        public boolean islike;

        public RecipeData(String cookbookName, String imgUrl, int collection, int like, int imgWidth, int imgHight, String obj) {
            this.cookbookName = cookbookName;
            this.imgUrl = imgUrl;
            this.collection = collection;
            this.like = like;
            this.obj = obj;
            this.imgWidth = imgWidth;
            this.imgHight = imgHight;
            this.isCollect = false;
            this.islike = false;
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

    private class WaterFallAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardViewHolder holder = new CardViewHolder(LayoutInflater.from(RecipeListActivity.this)
                    .inflate(R.layout.item_recipe_card, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final RecipeData data = cookBooks.get(position);
            if (holder instanceof CardViewHolder) {
                setCardViewColor((CardViewHolder) holder, data.islike, data.isCollect);
                ((CardViewHolder) holder).tv.setText(data.cookbookName);
                ((CardViewHolder) holder).tvLike.setText(String.valueOf(data.like));
                ((CardViewHolder) holder).tvStar.setText(String.valueOf(data.collection));
                float cardWidth = (DimensUtil.getScreenWidthDip(RecipeListActivity.this) - 24) / 2;
                ((CardViewHolder) holder).iv.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , DimensUtil.dpTopx(RecipeListActivity.this, (int) (data.imgHight * cardWidth / data.imgWidth))));
                Imageloader.getInstance().loadImageByUrl(data.imgUrl
                        , data.imgWidth
                        , data.imgHight
                        , R.drawable.img_default
                        , ((CardViewHolder) holder).iv);

                ((CardViewHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RecipeListActivity.this, RecipeInfoActivity.class);
                        intent.putExtra("obj", data.obj);
                        startActivity(intent);
                    }
                });

                ((CardViewHolder) holder).likeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(data.islike){
                            data.like--;
                            data.islike = false;
                        }else {
                            data.like++;
                            data.islike = true;
                        }
                        ((CardViewHolder) holder).tvLike.setText(String.valueOf(data.like));
                        setCardViewColor((CardViewHolder) holder, data.islike, null);
                    }
                });

                ((CardViewHolder) holder).starView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(data.isCollect){
                            data.collection--;
                            data.isCollect = false;
                        }else {
                            data.collection++;
                            data.isCollect = true;
                        }
                        ((CardViewHolder) holder).tvStar.setText(String.valueOf(data.collection));
                        setCardViewColor((CardViewHolder) holder, null , data.isCollect);
                    }
                });
            }
        }

        private void setCardViewColor(CardViewHolder holder, Object islike, Object isCollect) {
            if(null!=islike){
                FontIconDrawable iconThumbUp = FontIconDrawable.inflate(RecipeListActivity.this, R.xml.icon_thumb_up);
                if((boolean)islike){
                    iconThumbUp.setTextColor(ThemeUtil.getThemeColor());
                    holder.tvLike.setTextColor(ThemeUtil.getThemeColor());
                    holder.ivLike.setImageDrawable(iconThumbUp);
                }else {
                    iconThumbUp.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.tvLike.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.ivLike.setImageDrawable(iconThumbUp);
                }
            }
            if(null!=isCollect){
                FontIconDrawable iconStarEmpty = FontIconDrawable.inflate(RecipeListActivity.this, R.xml.icon_star);
                if((boolean)isCollect){
                    iconStarEmpty.setTextColor(ThemeUtil.getThemeColor());
                    holder.tvStar.setTextColor(ThemeUtil.getThemeColor());
                    holder.ivStar.setImageDrawable(iconStarEmpty);
                }else {
                    iconStarEmpty.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.tvStar.setTextColor(getResources().getColor(R.color.txt_light_color));
                    holder.ivStar.setImageDrawable(iconStarEmpty);
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
            ImageView ivLike;
            TextView tvLike;
            ImageView ivStar;
            TextView tvStar;
            View likeView;
            View starView;

            public CardViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.img);
                tv = (TextView) itemView.findViewById(R.id.txt);
                ivLike = (ImageView) itemView.findViewById(R.id.like_img);
                tvLike = (TextView) itemView.findViewById(R.id.like_num);
                ivStar = (ImageView) itemView.findViewById(R.id.star_img);
                tvStar = (TextView) itemView.findViewById(R.id.star_num);
                likeView = itemView.findViewById(R.id.like_click_range);
                starView = itemView.findViewById(R.id.star_click_range);
            }
        }
    }
}
