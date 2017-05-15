package com.czk.diabetes.recipe;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseFragmentActivity;
import com.czk.diabetes.DB.DBOpenHelper;
import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.TimeUtil;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.SpinerPopWindow;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 陈忠凯 on 2017/5/15.
 */

public class AddRecipeActivity extends BaseFragmentActivity {
    public final static String INTENT_ADD_TIME = "add_time";
    private String addTimeIntent;

    private static final int HANDLER_QUERY_FINSH = 0;

    //检查是否输入
    private EatRecord record;
    final private static int CHECK_NAME = 0;
    final private static int CHECK_MATERIAL = 1;
    final private static int CHECK_BEFORE = 2;
    final private static int CHECK_AFTER = 3;
    final private static int CHECK_SAVE = 4;

    private ImageView ivIcon;
    private EditText etName;
    private EditText etMaterial;
    private SpinerPopWindow spwDate;
    private List<String> typeChose;
    private TextView tvChoose;
    private TextView tvTime;
    private TextView tvDate;
    private EditText etBeforeDining;
    private EditText etAfterDinging;
    private EditText etRemarks;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_QUERY_FINSH:
                    setData();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        initData();
        initView();
        initAsncData();
        dealEvent();
    }

    private void initData() {
        typeChose = Arrays.asList(getResources().getStringArray(R.array.dining_type_chose));
        addTimeIntent = getIntent().getStringExtra(INTENT_ADD_TIME);
    }

    private void initAsncData() {
        if (null != addTimeIntent) {
            getData();
        }
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBOpenHelper helper = new DBOpenHelper(AddRecipeActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor c = db.query("eat_record"
                        , new String[]{"add_time", "name", "material", "type", "time", "before_dining", "after_dining", "description"}
                        , "add_time = ?"
                        , new String[]{addTimeIntent}
                        , null
                        , null
                        , "time" + " ASC");
                while (c.moveToNext()) {
                    record = new EatRecord(
                            c.getString(c.getColumnIndex("add_time")),
                            c.getString(c.getColumnIndex("name")),
                            c.getString(c.getColumnIndex("material")),
                            c.getString(c.getColumnIndex("type")),
                            c.getString(c.getColumnIndex("time")),
                            c.getString(c.getColumnIndex("before_dining")),
                            c.getString(c.getColumnIndex("after_dining")),
                            c.getString(c.getColumnIndex("description")));
                    break;
                }
                if (null == record) {
                    finish();
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.data_eorr));
                }
                handler.sendEmptyMessage(HANDLER_QUERY_FINSH);
                c.close();
                db.close();
            }
        }).start();
    }

    private void setData() {
        etName.setText(record.name);
        etMaterial.setText(record.material);
        tvChoose.setText(record.type);
        tvTime.setText(TimeUtil.getYearMonthDay(Long.parseLong(record.addTime)) + " " + record.time);
        etBeforeDining.setText(record.beforeDining);
        etAfterDinging.setText(record.afterDining);
        etRemarks.setText(record.description);
    }

    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.add_leechdom));
        /**主体**/
        etName = (EditText) findViewById(R.id.et_name);
        etMaterial = (EditText) findViewById(R.id.et_material);
        tvChoose = (TextView) findViewById(R.id.tv_type_choose);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDate = (TextView) findViewById(R.id.tv_time);
        spwDate = new SpinerPopWindow(AddRecipeActivity.this, typeChose);
        etBeforeDining = (EditText) findViewById(R.id.et_before_dining);
        etAfterDinging = (EditText) findViewById(R.id.et_after_dining);
        etRemarks = (EditText) findViewById(R.id.et_remarks);

//        tvChoose.setText();
        tvTime.setText(TimeUtil.getTime(System.currentTimeMillis()));
    }


    private void dealEvent() {
        //头部
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.chose_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spwDate.setWidth(v.getMeasuredWidth());
                spwDate.showAsDropDown(v);
            }
        });

        findViewById(R.id.chose_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                tvTime.setText(hourOfDay + ":" + minute);
                            }
                        }
                        , TimeUtil.getHourOfTheDay(System.currentTimeMillis())
                        , TimeUtil.getMinuteOfTheHour(System.currentTimeMillis())
                        , false).show(getSupportFragmentManager(), "timePicker");
            }
        });

        spwDate.setItemListener(new SpinerPopWindow.SpinerAdapter.OnItemSelectListener() {
            @Override
            public void onItemClick(int pos, String item) {
                tvChoose.setText(item);
            }
        });

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEvent(CHECK_SAVE)) {
                    DBOpenHelper helper = new DBOpenHelper(AddRecipeActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();  //得到的是SQLiteDatabase对象
                    long time = System.currentTimeMillis();
                    StringBuffer sBuffer = new StringBuffer();
                    sBuffer.append("REPLACE INTO eat_record");
                    sBuffer.append("(add_time,name,material,type,time,before_dining,after_dining,description) values (");
                    sBuffer.append("'" + time + "',");
                    sBuffer.append("'" + etName.getText() + "',");
                    sBuffer.append("'" + etMaterial.getText() + "',");
                    sBuffer.append("'" + tvChoose.getText() + "',");
                    sBuffer.append("'" + tvTime.getText() + "',");
                    sBuffer.append("'" + etBeforeDining.getText() + "',");
                    sBuffer.append("'" + etAfterDinging.getText() + "',");
                    sBuffer.append("'" + etRemarks.getText() + "'");
                    sBuffer.append(")");
                    // 执行创建表的SQL语句
                    db.execSQL(sBuffer.toString());
                    db.close();
                    finish();
                }
            }
        });

    }

    private boolean checkEvent(int type) {
        boolean doEvent = true;
        if (type > CHECK_NAME && StringUtil.isEmpty(etName.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddRecipeActivity.this, getResources().getString(R.string.please_input_food_name));
            etName.setFocusable(true);
            etName.setFocusableInTouchMode(true);
            etName.requestFocus();
            etName.findFocus();
        } else if (type > CHECK_MATERIAL && StringUtil.isEmpty(etMaterial.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddRecipeActivity.this, getResources().getString(R.string.please_input_food_material));
            etMaterial.setFocusable(true);
            etMaterial.setFocusableInTouchMode(true);
            etMaterial.requestFocus();
            etMaterial.findFocus();
        } else if (type > CHECK_BEFORE && StringUtil.isEmpty(etBeforeDining.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddRecipeActivity.this, getResources().getString(R.string.please_input_blood_sugar_level_before_dining));
            etBeforeDining.setFocusable(true);
            etBeforeDining.setFocusableInTouchMode(true);
            etBeforeDining.requestFocus();
            etBeforeDining.findFocus();
        } else if (type > CHECK_AFTER && StringUtil.isEmpty(etAfterDinging.getText().toString())) {
            doEvent = false;
            ToastUtil.showShortToast(AddRecipeActivity.this, getResources().getString(R.string.please_input_blood_sugar_level_after_dining));
            etAfterDinging.setFocusable(true);
            etAfterDinging.setFocusableInTouchMode(true);
            etAfterDinging.requestFocus();
            etAfterDinging.findFocus();
        }
        return doEvent;
    }
}
