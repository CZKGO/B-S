package com.czk.diabetes.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.czk.diabetes.util.LogUtil;

import java.util.Calendar;

/**
 * Created by 陈忠凯 on 2017/4/20.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationItem item = new NotificationItem();
        item.setTitle(intent.getStringExtra("title"));
        item.setDescription(intent.getStringExtra("Description"));
        String remind_number = intent.getStringExtra("remind_number");//提醒的日期
        LogUtil.d("sdafasfa","2");
        switch (item.getType()) {

            case NotificationItem.TYPE_DIY_DAY://只提醒一次
                LogUtil.d("sdafasfa","3");
                MYNotificationManager.getInstance().showNotifition(item, context);
                MYNotificationManager.getInstance().cancleAlarm(intent, context);
                break;
            case NotificationItem.TYPE_EVERY_DAY://每天

                MYNotificationManager.getInstance().showNotifition(item, context);

                break;
            case NotificationItem.TYPE_WEEK://每周自定义
                String[] arrr = remind_number.split(",");
                Calendar cc = Calendar.getInstance();
                int weeks = cc.get(Calendar.DAY_OF_WEEK);
                String Tweeks = (weeks - 1) + "";
                for (int i = 0; i < arrr.length; i++) {
                    if (arrr[i].equals(Tweeks)) {
                        MYNotificationManager.getInstance().showNotifition(item, context);
                    }
                }
                break;
        }
    }


}
