package com.czk.diabetes.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/5/8.
 */

public class MedicineActivity extends BaseActivity{
    private ImageView ivIcon;
    private View laoutAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
//        initData();
        initView();
        dealEvent();
    }



    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.leechdom));
        /**主体**/
        FontIconDrawable iconPill = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_pill);
        iconPill.setTextColor(getResources().getColor(R.color.white));
        FontIconDrawable iconClipboardEdit = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_clipboard_edit);
        iconClipboardEdit.setTextColor(getResources().getColor(R.color.white));
        ImageView ivPill = (ImageView) findViewById(R.id.left_icon);
        ImageView ivHistory = (ImageView) findViewById(R.id.right_icon);
        ivPill.setImageDrawable(iconPill);
        ivHistory.setImageDrawable(iconClipboardEdit);

        /**添加**/
        FontIconDrawable iconPlus = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_plus);
        iconPlus.setTextColor(getResources().getColor(R.color.txt_light_color));
        ImageView ivPlus = (ImageView) findViewById(R.id.add_icon);
        ivPlus.setImageDrawable(iconPlus);
        laoutAdd = findViewById(R.id.add_layout);


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
                Intent intent = new Intent(MedicineActivity.this, SearchMedicineActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.right_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicineActivity.this, MedicineHistoryActivity.class);
                startActivity(intent);
            }
        });

        laoutAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicineActivity.this, AddMedicineActivity.class);
                startActivity(intent);
            }
        });
    }
}
