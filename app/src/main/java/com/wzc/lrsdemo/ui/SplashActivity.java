package com.wzc.lrsdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wzc.lrsdemo.R;
import com.wzc.lrsdemo.base.App;
import com.wzc.lrsdemo.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private static Handler mHandle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (App.hasActivity(MainActivity.class)) {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            init();
        }

    }

    private void init() {
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }


}
