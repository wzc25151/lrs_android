package com.wzc.lrsdemo.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.wzc.lrsdemo.agora.openacall.model.ConstantApp;
import com.wzc.lrsdemo.manager.DialogManager;
import com.wzc.lrsdemo.utils.LogUtil;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/3/7.
 */

public class BaseActivity extends AppCompatActivity {
    private Toast mToast;//使用App级悬浮窗，偷懒
    protected static App app;//每个Activity持有App引用，偷懒

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = App.app;
        app.actList.add(this);
        LogUtil.e(app.actList.toString());
    }

    /**
     * 默认使用Activity调用Toast，或者可以用Application调用
     */
    public void showToast(String msg) {
        App.showToast(msg);
//        if (msg == null && msg.length() == 0) {
//            return;
//        }
//        if (mToast == null) {
//            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
//        }
//        LogUtil.e(msg);
//        mToast.setText(msg);
//        mToast.show();
    }

    /**
     * 检查权限，录音，文件访问
     */
    protected boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        LogUtil.e("checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            app.initWorkerThread();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        app.actList.remove(this);
        LogUtil.e(app.actList.toString());
        super.onDestroy();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
