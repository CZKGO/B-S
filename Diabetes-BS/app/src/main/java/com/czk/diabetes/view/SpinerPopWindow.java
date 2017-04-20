package com.czk.diabetes.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.czk.diabetes.R;

import java.util.List;

/**
 * Created by 陈忠凯 on 2017/4/17.
 */

public class SpinerPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private List<String> mObjects;
    private SpinerPopWindow.SpinerAdapter mAdapter;
    private SpinerPopWindow.SpinerAdapter.OnItemSelectListener mItemSelectListener;


    public SpinerPopWindow(Context context, List<String> mObjects) {
        super(context);
        mContext = context;
        this.mObjects = mObjects;
        init();
    }


    public void setItemListener(SpinerPopWindow.SpinerAdapter.OnItemSelectListener listener) {
        mItemSelectListener = listener;
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        //去掉黑边
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        mAdapter = new SpinerAdapter(mContext, mObjects);
        mListView.setAdapter(mAdapter);
    }


    public void refreshData(List<String> list, int selIndex) {
        if (list != null && selIndex != -1) {
            if (mAdapter != null) {
                mAdapter.refreshData(list, selIndex);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(pos, mObjects.get(pos));
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * Created by 陈忠凯 on 2017/4/17.
     */

    public static class SpinerAdapter extends BaseAdapter {

        public interface OnItemSelectListener {
            void onItemClick(int pos, String item);
        }

        private List<String> mObjects;

        private LayoutInflater mInflater;

        public SpinerAdapter(Context context, List<String> mObjects) {
            this.mObjects = mObjects;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public void refreshData(List<String> objects, int selIndex) {
            mObjects = objects;
            if (selIndex < 0) {
                selIndex = 0;
            }
            if (selIndex >= mObjects.size()) {
                selIndex = mObjects.size() - 1;
            }
        }


        @Override
        public int getCount() {

            return mObjects.size();
        }

        @Override
        public Object getItem(int pos) {
            return mObjects.get(pos).toString();
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_spinner, null);
                viewHolder = new ViewHolder();
                viewHolder.mTextView = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //Object item =  getItem(pos);
            viewHolder.mTextView.setText(mObjects.get(pos));

            return convertView;
        }


        public static class ViewHolder {
            public TextView mTextView;
        }
    }
}
