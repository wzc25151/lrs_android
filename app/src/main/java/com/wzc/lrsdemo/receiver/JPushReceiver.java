package com.wzc.lrsdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wzc.lrsdemo.utils.LogUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/3/13.
 */

public class JPushReceiver extends BroadcastReceiver {
    public static final String JPUSH_REGISTRATION = "cn.jpush.android.intent.REGISTRATION";
    public static final String JPUSH_MESSAGE_RECEIVED = "cn.jpush.android.intent.MESSAGE_RECEIVED";
    public static final String JPUSH_NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
    public static final String JPUSH_NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";
    public static final String JPUSH_NOTIFICATION_CLICK_ACTION = "cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION";
    public static final String JPUSH_CONNECTION = "cn.jpush.android.intent.CONNECTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        switch (action) {
            case JPUSH_REGISTRATION:
                LogUtil.e(JPUSH_REGISTRATION);
                break;
            case JPUSH_MESSAGE_RECEIVED:
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                LogUtil.e(JPUSH_MESSAGE_RECEIVED + "\t" + message);
                break;
            case JPUSH_NOTIFICATION_RECEIVED:
                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                LogUtil.e(JPUSH_NOTIFICATION_RECEIVED + "\t" + content);
                break;
            case JPUSH_NOTIFICATION_OPENED:
                LogUtil.e(JPUSH_NOTIFICATION_OPENED);
                break;
            case JPUSH_NOTIFICATION_CLICK_ACTION:
                LogUtil.e(JPUSH_NOTIFICATION_CLICK_ACTION);
                break;
            case JPUSH_CONNECTION:
                LogUtil.e(JPUSH_CONNECTION);
                break;
        }
    }
}
