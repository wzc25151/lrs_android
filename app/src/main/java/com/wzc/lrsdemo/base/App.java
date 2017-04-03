package com.wzc.lrsdemo.base;

import android.app.Application;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.wzc.lrsdemo.agora.openacall.model.CurrentUserSettings;
import com.wzc.lrsdemo.agora.openacall.model.WorkerThread;
import com.wzc.lrsdemo.manager.HttpManager;
import com.wzc.lrsdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/3/6.
 */

public class App extends Application {
    public static App app;
    private static Toast mToast;
    public static List<BaseActivity> actList = new ArrayList<BaseActivity>();


    public static final CurrentUserSettings mAudioSettings = new CurrentUserSettings();//声网

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        init();
    }

    private void init() {
//        PgyCrashManager.register(app);
        HttpManager.initializeDefaultHttpsClient(app);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(app);
    }

    public static boolean hasActivity(Class clazz) {
        if (clazz == null) {
            return false;
        }
        for (int i = 0; i < actList.size(); i++) {
            if (clazz.equals(actList.get(i).getClass())) {
                return true;
            }
        }
        return false;
    }

    public static void showToast(String msg) {
        if (msg == null && msg.length() == 0) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(app, msg, Toast.LENGTH_SHORT);
        }
        LogUtil.e(msg);
        mToast.setText(msg);
        mToast.show();
    }

    private WorkerThread mWorkerThread;

    /**
     * 声网工作线程初始化
     */
    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(app);
            mWorkerThread.start();
            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    /**
     * 声网工作线程销毁
     */
    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

}