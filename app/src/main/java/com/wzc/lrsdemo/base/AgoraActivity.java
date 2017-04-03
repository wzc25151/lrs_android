package com.wzc.lrsdemo.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.wzc.lrsdemo.R;
import com.wzc.lrsdemo.agora.openacall.model.AGEventHandler;
import com.wzc.lrsdemo.agora.openacall.model.ConstantApp;
import com.wzc.lrsdemo.agora.openacall.model.CurrentUserSettings;
import com.wzc.lrsdemo.agora.openacall.model.EngineConfig;
import com.wzc.lrsdemo.agora.openacall.model.MyEngineEventHandler;
import com.wzc.lrsdemo.agora.openacall.model.WorkerThread;
import com.wzc.lrsdemo.agora.propeller.headset.HeadsetBroadcastReceiver;
import com.wzc.lrsdemo.agora.propeller.headset.HeadsetPlugManager;
import com.wzc.lrsdemo.agora.propeller.headset.IHeadsetPlugListener;
import com.wzc.lrsdemo.agora.propeller.headset.bluetooth.BluetoothHeadsetBroadcastReceiver;
import com.wzc.lrsdemo.utils.LogUtil;

import java.util.Arrays;
import java.util.List;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

/**
 * Created by Administrator on 2017/3/8.
 */

public class AgoraActivity extends BaseActivity implements AGEventHandler, IHeadsetPlugListener {
    //默认禁言
    private volatile boolean mAudioMuted = false;
    //默认使用听筒
    private volatile boolean mEarpiece = true;

    private volatile boolean mWithHeadset = false;

    private HeadsetBroadcastReceiver mHeadsetListener;
    private BluetoothAdapter mBtAdapter;
    private BluetoothProfile mBluetoothProfile;
    private BluetoothHeadsetBroadcastReceiver mBluetoothHeadsetBroadcastListener;

    private BluetoothProfile.ServiceListener mBluetoothHeadsetListener = new BluetoothProfile.ServiceListener() {

        /**
         * 监听到蓝牙设备连接
         * @param profile
         * @param headset
         */
        @Override
        public void onServiceConnected(int profile, BluetoothProfile headset) {
            if (profile == BluetoothProfile.HEADSET) {
                LogUtil.e("onServiceConnected " + profile + " " + headset);
                mBluetoothProfile = headset;

                List<BluetoothDevice> devices = headset.getConnectedDevices();
                headsetPlugged(devices != null && devices.size() > 0);
            }
        }

        /**
         * 蓝牙设备断开
         * @param profile
         */
        @Override
        public void onServiceDisconnected(int profile) {
            LogUtil.e("onServiceDisconnected " + profile);
            mBluetoothProfile = null;
        }
    };

    /**
     * 是否使用蓝牙耳机
     *
     * @param plugged
     */
    private void headsetPlugged(final boolean plugged) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isFinishing()) {
                    return;
                }

                RtcEngine rtcEngine = rtcEngine();
                rtcEngine.setEnableSpeakerphone(!plugged);
            }
        }).start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                boolean checkPermissionResult = checkSelfPermissions();
                if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
                    // so far we do not use OnRequestPermissionsResultCallback
                }
            }
        }, 500);
    }

    /**
     * 获取录音推流的引擎
     *
     * @return
     */
    protected RtcEngine rtcEngine() {
        return app.getWorkerThread().getRtcEngine();
    }

    /**
     * 获取录音推流实例线程
     *
     * @return
     */
    protected final WorkerThread worker() {
        return app.getWorkerThread();
    }

    /**
     * 获取推流的配置属性
     *
     * @return
     */
    protected final EngineConfig config() {
        return app.getWorkerThread().getEngineConfig();
    }

    /**
     * 获得推流事件处理器
     *
     * @return
     */
    protected final MyEngineEventHandler event() {
        return app.getWorkerThread().eventHandler();
    }

    /**
     * 获得录音属性设置对象
     *
     * @return
     */
    protected CurrentUserSettings vSettings() {
        return App.mAudioSettings;
    }

    /**
     * 虚拟按键高度调整
     */
    protected int virtualKeyHeight() {
        boolean hasPermanentMenuKey = ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(getApplication()));
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
        } else {
            display.getMetrics(metrics);
        }
        int fullHeight = metrics.heightPixels;
        display.getMetrics(metrics);
        return fullHeight - metrics.heightPixels;
    }

    //初始化
    protected void initUIandEvent() {
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                event().addEventHandler(AgoraActivity.this);
                Intent i = getIntent();
                String channelName = i.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
                worker().joinChannel(channelName, config().mUid);
                optional();
            }
        });
    }

    //头戴式录音来源
    private void optional() {
        HeadsetPlugManager.getInstance().registerHeadsetPlugListener(this);
        mHeadsetListener = new HeadsetBroadcastReceiver();
        registerReceiver(mHeadsetListener, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        mBluetoothHeadsetBroadcastListener = new BluetoothHeadsetBroadcastReceiver();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null && BluetoothProfile.STATE_CONNECTED == mBtAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
            // on some devices, BT is not supported
            boolean bt = mBtAdapter.getProfileProxy(getBaseContext(), mBluetoothHeadsetListener, BluetoothProfile.HEADSET);
            int connection = mBtAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        }
        IntentFilter i = new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        i.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        i.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        registerReceiver(mBluetoothHeadsetBroadcastListener, i);
//避免对window添加ui修改参数
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    //销毁时
    protected void deInitUIandEvent() {
        optionalDestroy();
        doLeaveChannel();
        event().removeEventHandler(this);
    }

    //销毁时操作
    private void optionalDestroy() {
        if (mBtAdapter != null) {
            mBtAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothProfile);
            mBluetoothProfile = null;
            mBtAdapter = null;
        }
        if (mBluetoothHeadsetBroadcastListener != null) {
            unregisterReceiver(mBluetoothHeadsetBroadcastListener);
            mBluetoothHeadsetBroadcastListener = null;
        }
        if (mHeadsetListener != null) {
            unregisterReceiver(mHeadsetListener);
            mHeadsetListener = null;
        }
        HeadsetPlugManager.getInstance().unregisterHeadsetPlugListener(this);
    }
//加入房间之前的参数
//    vSettings().mChannelName =""+ channel;
//    Intent i = new Intent(MainActivity.this, ChatActivity.class);
//    i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, channel);
//    startActivity(i);

//开始发言，反之取消发言
//    RtcEngine rtcEngine = rtcEngine();
//rtcEngine.muteLocalAudioStream(mAudioMuted = false);

    /**
     * 离开房间
     */
    private void doLeaveChannel() {
        LogUtil.e("我离开了房间");
        worker().leaveChannel(config().mChannel);
    }

    /**
     * 房间连接成功
     *
     * @param channel
     * @param uid
     * @param elapsed
     */
    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        String msg = "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed;
        LogUtil.e(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                rtcEngine().muteLocalAudioStream(mAudioMuted = true);
//                worker().getRtcEngine().setEnableSpeakerphone(!mEarpiece);
                mEarpiece = false;
                worker().getRtcEngine().setEnableSpeakerphone(!mEarpiece);

            }
        });
    }

    /**
     * 掉线
     *
     * @param uid
     * @param reason
     */
    @Override
    public void onUserOffline(int uid, int reason) {
        String msg = "onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason;
        LogUtil.e(msg);
    }

    /**
     * 带着耳机的时候，plugged为true表示带着耳机
     *
     * @param plugged
     * @param extraData
     */
    @Override
    public void notifyHeadsetPlugged(final boolean plugged, Object... extraData) {
        LogUtil.e(plugged + " " + extraData);
//        log.info("notifyHeadsetPlugged " + plugged + " " + extraData);
        boolean bluetooth = false;
        if (extraData != null && extraData.length > 0 && (Integer) extraData[0] == HeadsetPlugManager.BLUETOOTH) { // this is only for bluetooth
            bluetooth = true;
        }
        headsetPlugged(plugged);
    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                doHandleExtraCallback(type, data);
            }
        });
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;
        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_AUDIO_MUTED:
                peerUid = (Integer) data[0];
                muted = (boolean) data[1];
                LogUtil.e("mute: " + (peerUid & 0xFFFFFFFFL) + " " + muted);
//                notifyMessageChanged("mute: " + (peerUid & 0xFFFFFFFFL) + " " + muted);
                break;
            case AGEventHandler.EVENT_TYPE_ON_AUDIO_QUALITY:
                peerUid = (Integer) data[0];
                int quality = (int) data[1];
                short delay = (short) data[2];
                short lost = (short) data[3];

                LogUtil.e("quality: " + (peerUid & 0xFFFFFFFFL) + " " + quality + " " + delay + " " + lost);
//                notifyMessageChanged("quality: " + (peerUid & 0xFFFFFFFFL) + " " + quality + " " + delay + " " + lost);
                break;
            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS:
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];
                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }
                StringBuilder volumeCache = new StringBuilder();
                for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                    peerUid = each.uid;
                    int peerVolume = each.volume;
                    if (peerUid == 0) {
                        continue;
                    }
                    volumeCache.append("volume: ").append(peerUid & 0xFFFFFFFFL).append(" ").append(peerVolume).append("\n");
                }
                if (volumeCache.length() > 0) {
                    LogUtil.e(volumeCache.substring(0, volumeCache.length() - 1));
//                    notifyMessageChanged(volumeCache.substring(0, volumeCache.length() - 1));
                }
                break;
            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR:
                int subType = (int) data[0];
                if (subType == ConstantApp.AppError.NO_NETWORK_CONNECTION) {
                    LogUtil.e(getString(R.string.msg_no_network_connection));
//                    showLongToast(getString(R.string.msg_no_network_connection));
                }
                break;
            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];
                LogUtil.e(error + " " + description);
//                notifyMessageChanged(error + " " + description);
                break;
            }
        }
    }
}