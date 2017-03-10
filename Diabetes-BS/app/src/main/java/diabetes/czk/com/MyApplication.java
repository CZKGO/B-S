package diabetes.czk.com;

import android.app.Application;

import diabetes.czk.com.util.FontIconDrawable;

/**
 * Created by 陈忠凯 on 2017/2/18.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontIconDrawable.init(getAssets(),"font_icon.ttf");
    }

}
