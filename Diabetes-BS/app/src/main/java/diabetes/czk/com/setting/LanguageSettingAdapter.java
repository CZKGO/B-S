package diabetes.czk.com.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import diabetes.czk.com.R;

/**
 * Created by 陈忠凯 on 2017/3/6.
 */
public class LanguageSettingAdapter extends BaseAdapter{
    private String[] languageList;
    private Context context;
    private int selectIndex;

    public LanguageSettingAdapter(Context context, String[] languageList, int selectIndex) {
        this.languageList = languageList;
        this.context = context;
        this.selectIndex = selectIndex;

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return languageList.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return languageList[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.item_language_layout, null);
            holder=new ViewHolder();
            holder.txt_lang = (TextView) view.findViewById(R.id.txt_lang);
            holder.img_selected_lang = (ImageView) view.findViewById(R.id.img_selected_lang);
            view.setTag(holder);
        } else {
            holder=(ViewHolder)view.getTag();
        }

        if (selectIndex == position) {
            holder.img_selected_lang.setVisibility(View.VISIBLE);
        } else {
            holder.img_selected_lang.setVisibility(View.GONE);
        }

        holder.txt_lang.setText(languageList[position]);

        return view;
    }

    public void setSelectIndex(int index) {
        this.selectIndex = index;
    }

    class ViewHolder{
        TextView txt_lang;
        ImageView img_selected_lang;
    }
}
