package diabetes.czk.com.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import diabetes.czk.com.R;

/**
 * Created by 陈忠凯 on 2017/3/10.
 */
public class PieChartView extends View {

    private float startAngle = 0;
    private Map<Float,Integer> datas;

    public PieChartView(Context context) {
        super(context);
        init(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieChartView);
        startAngle = a.getFloat(R.styleable.PieChartView_start_angle, 0);
        a.recycle();

        Map<Float,Integer> datas = new HashMap<>();
        datas.put(1f, Color.LTGRAY);
        datas.put(2f, Color.YELLOW);
        datas.put(3f, Color.RED);
        setDatasAndColors(datas);
    }

    private void setDatasAndColors(Map<Float, Integer> datas) {
        this.datas = datas;
        for(int i = 0;i<datas.size();i++){
            datas.get()
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for(int i = 0;i<datas.size();i++){
            drawArc();
        }
    }

}
