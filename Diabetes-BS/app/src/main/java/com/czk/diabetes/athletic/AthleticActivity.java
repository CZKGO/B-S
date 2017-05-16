package com.czk.diabetes.athletic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.DB.DBOpenHelper;
import com.czk.diabetes.R;
import com.czk.diabetes.recipe.EatRecord;
import com.czk.diabetes.recipe.RecipeHistoryActivity;
import com.czk.diabetes.recipe.RecipeListActivity;
import com.czk.diabetes.util.FontIconDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/5/8.
 */

public class AthleticActivity extends BaseActivity{
    private static final int QUERY_FINSH = 0;
    private ImageView ivIcon;
    private View laoutAdd;
    private List<EatRecord> records = new ArrayList<>();
    private ListView lvRecord;
    private RecordAdapter adapter;
    private ImageView ivRightIcon;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUERY_FINSH:
                    if (records.size() > 0) {
                        laoutAdd.setVisibility(View.GONE);
                        lvRecord.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        ivRightIcon.setVisibility(View.VISIBLE);
                    } else {
                        laoutAdd.setVisibility(View.VISIBLE);
                        lvRecord.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        ivRightIcon.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athletic);
//        initData();
        initView();
        dealEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                records.clear();
                DBOpenHelper helper = new DBOpenHelper(AthleticActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("eat_record"
                        , new String[]{"add_time", "name", "material", "type", "time", "before_dining", "after_dining", "description"}
                        , null
                        , null
                        , null
                        , null
                        , "add_time" + " ASC");
                while (c.moveToNext()) {
                    EatRecord data =  new EatRecord(
                            c.getString(c.getColumnIndex("add_time")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("material")),
                            c.getString(c.getColumnIndex("type")),
                            c.getString(c.getColumnIndex("time")),
                            c.getString(c.getColumnIndex("before_dining")),
                            c.getString(c.getColumnIndex("after_dining")),
                            c.getString(c.getColumnIndex("description")));
                    records.add(data);
                }
                handler.sendEmptyMessage(QUERY_FINSH);
                c.close();
                db.close();
            }
        }).start();
    }

    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.athletics));

        /**主体**/
        FontIconDrawable iconRoomService = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_directions_run);
        iconRoomService.setTextColor(getResources().getColor(R.color.white));
        FontIconDrawable iconClipboardEdit = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_clipboard_edit);
        iconClipboardEdit.setTextColor(getResources().getColor(R.color.white));
        ImageView ivRoomService = (ImageView) findViewById(R.id.left_icon);
        ImageView ivHistory = (ImageView) findViewById(R.id.right_icon);
        ivRoomService.setImageDrawable(iconRoomService);
        ivHistory.setImageDrawable(iconClipboardEdit);

        /**添加**/
        FontIconDrawable iconPlus = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_plus);
        iconPlus.setTextColor(getResources().getColor(R.color.txt_light_color));
        ImageView ivPlus = (ImageView) findViewById(R.id.add_icon);
        ivPlus.setImageDrawable(iconPlus);
        laoutAdd = findViewById(R.id.add_layout);

        ivRightIcon = (ImageView) findViewById(R.id.title_right_icon);
        ivRightIcon.setImageDrawable(iconPlus);

        lvRecord = (ListView) findViewById(R.id.list);
        adapter = new RecordAdapter(AthleticActivity.this, records);
        lvRecord.setAdapter(adapter);
    }

    private void dealEvent() {
        //头部
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //主体
        findViewById(R.id.left_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AthleticActivity.this, RecipeListActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.right_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AthleticActivity.this, RecipeHistoryActivity.class);
                startActivity(intent);
            }
        });

        laoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AthleticActivity.this, AddAthleticActivity.class);
                startActivity(intent);
            }
        });

        ivRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AthleticActivity.this, AddAthleticActivity.class);
                startActivity(intent);
            }
        });

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AthleticActivity.this, AddAthleticActivity.class);
                intent.putExtra(AddAthleticActivity.INTENT_ADD_TIME, records.get(position).addTime);
                startActivity(intent);
            }
        });
    }

    private class RecordAdapter extends BaseAdapter {
        private List<EatRecord> records;
        private Context context;

        public RecordAdapter(Context context, List<EatRecord> records) {
            this.records = records;
            this.context = context;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return records.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;
            EatRecord record = records.get(position);
            if (null == view) {
                view = LayoutInflater.from(context).inflate(R.layout.item_medicine_record_layout, null);
                holder = new ViewHolder();
                holder.txtNamw = (TextView) view.findViewById(R.id.tv_name);
                holder.txtDoses = (TextView) view.findViewById(R.id.tv_doses);
                holder.txtTime = (TextView) view.findViewById(R.id.tv_time);
                holder.ivIcon = (ImageView) view.findViewById(R.id.icon);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.txtNamw.setText(record.name);
            float dValue = Float.parseFloat(record.afterDining) - Float.parseFloat(record.beforeDining);
            String value = "";
            if(dValue<0){
                value = "-"+Math.abs(dValue);
            }else {
                value = "+"+dValue;
            }
            holder.txtDoses.setText(value + getResources().getString(R.string.mmol_l));
            holder.txtTime.setText(records.get(position).time);
            holder.ivIcon.setImageDrawable(FontIconDrawable.inflate(context, R.xml.icon_directions_run));
            return view;
        }

        class ViewHolder {
            TextView txtNamw;
            TextView txtDoses;
            TextView txtTime;
            ImageView ivIcon;
        }
    }
}
