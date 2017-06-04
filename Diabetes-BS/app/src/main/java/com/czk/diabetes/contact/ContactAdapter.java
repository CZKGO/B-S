package com.czk.diabetes.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.setting.ThemeChoseDialog;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.ThemeUtil;

import java.util.List;

/**
 * Created by xuezaishao on 2017/6/4.
 */

class ContactAdapter extends BaseAdapter {
    private List<ContactData> contactList;

    public ContactAdapter(List<ContactData> contactList) {
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        final ContactData data = (ContactData) getItem(position);
        if (null == view) {
            view = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_contact_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(data.type == ContactData.TYPE_SEND){
            holder.tvName.setText(data.usetName);
            Imageloader.getInstance().loadImageByUrl(data.userImg
                    , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                    , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                    , R.drawable.default_people
                    , holder.ivIcon
                    , true);
            holder.tvContent.setText(data.text);
            holder.viewLeft.setVisibility(View.VISIBLE);
            holder.viewRight.setVisibility(View.GONE);
        }else {
            holder.tvNameRight.setText(data.doctorName);
            Imageloader.getInstance().loadImageByUrl(data.doctorImg
                    , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                    , DimensUtil.dpTopx(MyApplication.getInstance(), 40)
                    , R.drawable.default_people
                    , holder.ivIconRight
                    , true);
            holder.tvContentRight.setText(data.text);
            holder.viewLeft.setVisibility(View.GONE);
            holder.viewRight.setVisibility(View.VISIBLE);
        }
        return view;
    }
    class ViewHolder {
        View itemView;
        View viewLeft;
        View viewRight;
        TextView tvName;
        TextView tvContent;
        ImageView ivIcon;
        TextView tvNameRight;
        TextView tvContentRight;
        ImageView ivIconRight;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            viewLeft =  itemView.findViewById(R.id.left_layout);
            viewRight =  itemView.findViewById(R.id.right_layout);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvNameRight = (TextView) itemView.findViewById(R.id.tv_name_right);
            tvContentRight = (TextView) itemView.findViewById(R.id.tv_content_right);
            ivIconRight = (ImageView) itemView.findViewById(R.id.iv_icon_right);
        }
    }
}
