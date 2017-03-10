package diabetes.czk.com;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class MedicineFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_mainpage = inflater.inflate(R.layout.medicine_mainpage, container, false);
        return fragment_mainpage;
    }
}
