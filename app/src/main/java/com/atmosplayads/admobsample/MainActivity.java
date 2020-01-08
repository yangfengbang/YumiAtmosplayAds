package com.atmosplayads.admobsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.MobileAds;
import com.atmosplayads.atmosplayadsadmobdemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    static final String INTERSTITIAL_ID = "ca-app-pub-1050908092086969/2862473426";
    static final String BANNER_ID = "ca-app-pub-1050908092086969/3381348174";
    static final String VIDEO_ID = "ca-app-pub-1050908092086969/2068266509";
    static final String NEW_VIDEO_ID = "ca-app-pub-1050908092086969/6190170882";
    private static final String APP_ID = "ca-app-pub-1050908092086969~5619664554";

    Button btn_RewardedVideoAD_legacy, btn_InterstitialAD, btn_BannerAD, btn_RewardedVideoAD, btn_admob_debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, APP_ID);

        btn_RewardedVideoAD_legacy = findViewById(R.id.btn_RewardedVideoAD_legacy);
        btn_RewardedVideoAD_legacy.setOnClickListener(this);
        btn_RewardedVideoAD = findViewById(R.id.btn_RewardedVideoAD);
        btn_RewardedVideoAD.setOnClickListener(this);
        btn_InterstitialAD = findViewById(R.id.btn_InterstitialAD);
        btn_InterstitialAD.setOnClickListener(this);
        btn_BannerAD = findViewById(R.id.btn_BannerAD);
        btn_BannerAD.setOnClickListener(this);
        btn_admob_debug = findViewById(R.id.btn_admob_debug);
        btn_admob_debug.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.btn_BannerAD) {
            intent.setClass(MainActivity.this, BannerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_InterstitialAD) {
            intent.setClass(MainActivity.this, InterstitialActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_RewardedVideoAD_legacy) {
            intent.setClass(MainActivity.this, OldAdmobRewardedVideoApiActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_RewardedVideoAD) {
            intent.setClass(MainActivity.this, RewardedVideoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_admob_debug) {
            MediationTestSuite.launch(MainActivity.this, APP_ID);
        }
    }
}