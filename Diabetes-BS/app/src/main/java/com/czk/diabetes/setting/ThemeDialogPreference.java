package com.czk.diabetes.setting;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.czk.diabetes.R;
import com.czk.diabetes.util.ThemeUtil;

/**
 * Created by 陈忠凯 on 2017/5/4.
 */

public class ThemeDialogPreference extends Preference {
    private TextView tvTheme;
    private View imgTheme;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ThemeDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThemeDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemeDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemeDialogPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.preference_theme, parent, false);
        return layout;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        imgTheme = view.findViewById(R.id.img_theme);
        tvTheme = (TextView) view.findViewById(android.R.id.summary);
        tvTheme.setText(ThemeUtil.getThemeName());
    }

    @Override
    protected void onClick() {
        super.onClick();
        final ThemeChoseDialog dialog = new ThemeChoseDialog(getContext());
        dialog.setOnSelectListener(new ThemeChoseDialog.OnSelectListener() {
            @Override
            public void onSelect(String value) {
                OnPreferenceChangeListener listener = getOnPreferenceChangeListener();
                tvTheme.setText(value);
                imgTheme.setBackgroundColor(ThemeUtil.getThemeColor());
                if(listener!=null)
                    listener.onPreferenceChange(ThemeDialogPreference.this, value);
            }
        });
        dialog.show();
    }

}
