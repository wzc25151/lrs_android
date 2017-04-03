package com.wzc.lrsdemo.http;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by wangxiangbo on 2016/7/14.
 */
public class ToSendChatData {
    private final String url = "http://192.168.1.100:9898" + "";
    private String msg;
    private StringCallback callback;


    public ToSendChatData(String msg, StringCallback callback) {
        this.msg = msg;
        this.callback = callback;
    }

    public void submit() {
        OkHttpUtils
                .post()
                .url(url)
                .addParams("msg", msg)
                .build()
                .execute(callback);
    }
}
