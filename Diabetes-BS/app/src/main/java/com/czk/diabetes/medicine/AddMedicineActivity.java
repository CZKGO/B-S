package com.czk.diabetes.medicine;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/5/9.
 */

public class AddMedicineActivity extends BaseActivity{
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
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
        tvTitle.setText(getResources().getString(R.string.add_leechdom));
        /**主体**/
        ImageView ivAdd = (ImageView) findViewById(R.id.iv_add);
        FontIconDrawable iconPlus = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_plus);
        ivAdd.setImageDrawable(iconPlus);

        ImageView ivToggle = (ImageView) findViewById(R.id.iv_toggle);
        FontIconDrawable iconToggle = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_toggle_on);
        ivToggle.setImageDrawable(iconToggle);
    }


    private void dealEvent() {

    }
}
