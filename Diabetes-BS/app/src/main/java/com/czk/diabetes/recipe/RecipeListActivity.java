package com.czk.diabetes.recipe;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private ImageView ivIcon;
    private List<RecipeData> cookBooks = new ArrayList<>();
    private RecyclerView recyclerView;
    private WaterFallAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH_FINSH:
                    adapter.notifyDataSetChanged();
                    break;
                case SEARCH_ERRO:
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
        DiabetesClient.post(DiabetesClient.getZKTRECIPEAbsoluteUrl("getCookBooksNew")
                , DiabetesClient.getCookBooksNew()
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
                        });
                        searchThread.start();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        handler.sendEmptyMessage(SEARCH_ERRO);
                    }
                });
    }

    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.many_recipes));
        /**
         * 主体
         */
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
                            , keyArray.getJSONObject(i).getInt("picWidth")
                            , keyArray.getJSONObject(i).getInt("picHeight")
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
        private int imgWidth;
        private int imgHight;
        private String obj;

        public RecipeData(String cookbookName, String imgUrl,int imgWidth,int imgHight, String obj) {
            this.cookbookName = cookbookName;
            this.imgUrl = imgUrl;
            this.obj = obj;
            this.imgWidth = imgWidth;
            this.imgHight = imgHight;
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RecipeData data = cookBooks.get(position);
            if (holder instanceof CardViewHolder) {
                ((CardViewHolder) holder).tv.setText(data.cookbookName);
                float cardWidth = (DimensUtil.getScreenWidthDip(RecipeListActivity.this) - 24) / 2;
                ((CardViewHolder) holder).iv.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , DimensUtil.dpTopx(RecipeListActivity.this,(int) (data.imgHight*cardWidth/data.imgWidth))));
                Imageloader.getInstance().loadImageByUrl(data.imgUrl
                        ,data.imgWidth
                        ,data.imgHight
                        ,R.drawable.icon,((CardViewHolder) holder).iv);
            }
        }

        @Override
        public int getItemCount() {
            return cookBooks.size();
        }

        private class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView tv;

            public CardViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.img);
                tv = (TextView) itemView.findViewById(R.id.txt);
            }
        }
    }
}
