package diabetes.czk.com;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

import diabetes.czk.com.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/12.
 */
public class AddValueActivity extends BaseActivity {
    private ImageView ivIcon;
    private ImageView ivIconAdd;
    private EditText etValue;
    private TextView tvNumber0;
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvNumber3;
    private TextView tvNumber4;
    private TextView tvNumber5;
    private TextView tvNumber6;
    private TextView tvNumber7;
    private TextView tvNumber8;
    private TextView tvNumber9;
    private TextView tvNumberDelete;
    private TextView tvNumberPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);
        initView();
        dealEvent();
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.input_measured_value));
        //主体
        etValue = (EditText) findViewById(R.id.et_value);
         /*设置EditText光标可见但不弹出软键盘*/
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            etValue.setInputType(InputType.TYPE_NULL);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setSoftInputShownOnFocus;
                setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setSoftInputShownOnFocus.setAccessible(true);
                setSoftInputShownOnFocus.invoke(etValue, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //键盘
        tvNumber0 = (TextView) findViewById(R.id.number_0);
        tvNumber1 = (TextView) findViewById(R.id.number_1);
        tvNumber2 = (TextView) findViewById(R.id.number_2);
        tvNumber3 = (TextView) findViewById(R.id.number_3);
        tvNumber4 = (TextView) findViewById(R.id.number_4);
        tvNumber5 = (TextView) findViewById(R.id.number_5);
        tvNumber6 = (TextView) findViewById(R.id.number_6);
        tvNumber7 = (TextView) findViewById(R.id.number_7);
        tvNumber8 = (TextView) findViewById(R.id.number_8);
        tvNumber9 = (TextView) findViewById(R.id.number_9);
        tvNumberDelete = (TextView) findViewById(R.id.number_delete);
        tvNumberPoint = (TextView) findViewById(R.id.number_point);

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //主体
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //键盘
        setKeyOnClickListener(tvNumber0, 0);
        setKeyOnClickListener(tvNumber1, 1);
        setKeyOnClickListener(tvNumber2, 2);
        setKeyOnClickListener(tvNumber3, 3);
        setKeyOnClickListener(tvNumber4, 4);
        setKeyOnClickListener(tvNumber5, 5);
        setKeyOnClickListener(tvNumber6, 6);
        setKeyOnClickListener(tvNumber7, 7);
        setKeyOnClickListener(tvNumber8, 8);
        setKeyOnClickListener(tvNumber9, 9);
        setKeyOnClickListener(tvNumberDelete, -1);
        setKeyOnClickListener(tvNumberPoint, -2);
    }

    private void setKeyOnClickListener(TextView view, final int i) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = etValue.getText().toString();
                if (-1 == i) {
                    if(value.length()>0){
                        value = value.substring(0,value.length()-1);
                    }
                } else {
                    etValue.setText(value + i);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
