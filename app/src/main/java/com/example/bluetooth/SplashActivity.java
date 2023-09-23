package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    @SuppressLint("HandlerLeak")
    Handler mainHandler = new Handler(){};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goNext();
            }
        }, 1500);
    }

    public void goNext() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }
}
