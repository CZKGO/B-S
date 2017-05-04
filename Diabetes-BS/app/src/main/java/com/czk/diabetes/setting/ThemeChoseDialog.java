package com.czk.diabetes.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czk.diabetes.R;
import com.czk.diabetes.util.ThemeUtil;

/**
 * Created by 陈忠凯 on 2017/5/4.
 */

public class ThemeChoseDialog extends Dialog {

    private OnSelectListener onSelectListener;

    public ThemeChoseDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialo_theme_chose);
        ListView listView = (ListView) findViewById(R.id.list);
        ThemeListAdapter adapter = new ThemeListAdapter(ThemeUtil.getTheme()
                , getContext().getResources().getStringArray(R.array.themes));
        listView.setAdapter(adapter);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public  interface OnSelectListener {
        void onSelect(String value);
    }

    private class ThemeListAdapter extends BaseAdapter {
        private int selectIndex;
        private String[] stringArray;

        public ThemeListAdapter(int selectIndex, String[] stringArray) {
            this.selectIndex = selectIndex;
            this.stringArray = stringArray;
        }

        @Override
        public int getCount() {
            return stringArray.length;
        }

        @Override
        public Object getItem(int position) {
            return stringArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            final String data = (String) getItem(position);
            if (null == view) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_theme_list, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (getContext().getResources().getString(R.string.theme_pink).equals(data))
                holder.img_theme.setBackgroundColor(getContext().getResources().getColor(R.color.pink_color));
            else if (getContext().getResources().getString(R.string.theme_blue).equals(data))
                holder.img_theme.setBackgroundColor(getContext().getResources().getColor(R.color.blue_color));

            if (selectIndex == position) {
                holder.img_selected_lang.setVisibility(View.VISIBLE);
            } else {
                holder.img_selected_lang.setVisibility(View.GONE);
            }

            holder.txt_lang.setText(data);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext().getResources().getString(R.string.theme_pink).equals(data))
                        ThemeUtil.setTheme(ThemeUtil.THEME_PINK);
                    else if (getContext().getResources().getString(R.string.theme_blue).equals(data))
                        ThemeUtil.setTheme(ThemeUtil.THEME_BLUE);
                    if(onSelectListener!=null){
                        onSelectListener.onSelect(data);
                    }
                    dismiss();

                }
            });
            return view;
        }

        public void setSelectIndex(int index) {
            this.selectIndex = index;
        }

        class ViewHolder {
            View itemView;
            View img_theme;
            TextView txt_lang;
            ImageView img_selected_lang;

            public ViewHolder(View itemView) {
                this.itemView = itemView;
                img_theme = itemView.findViewById(R.id.img_theme);
                txt_lang = (TextView) itemView.findViewById(R.id.txt_lang);
                img_selected_lang = (ImageView) itemView.findViewById(R.id.img_selected_lang);
            }
        }
    }
}
