package diabetes.czk.com.setting;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import diabetes.czk.com.MainActivity;
import diabetes.czk.com.R;
import diabetes.czk.com.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/3/4.
 */
public class SettingActvity extends PreferenceActivity {
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        addPreferencesFromResource(R.xml.setting_perference);
        initView();
        dealEvent();
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        ivIcon.setImageDrawable(FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left));
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.setting));

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingActvity.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
