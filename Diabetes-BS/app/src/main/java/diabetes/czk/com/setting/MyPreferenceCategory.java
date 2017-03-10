package diabetes.czk.com.setting;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import diabetes.czk.com.R;

/**
 * Created by 陈忠凯 on 2017/3/6.
 */
public class MyPreferenceCategory extends PreferenceCategory {
    private Context context;
    private int titleColor;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyPreferenceCategory(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        final LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.preference_categoty, parent, false);
        return layout;
    }

    @Override
    protected void onBindView(View view) {
//        TextView title
        super.onBindView(view);
    }
}
