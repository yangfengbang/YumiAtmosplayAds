package com.atmosplayads.admobsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.atmosplayads.atmosplayadsadmobdemo.R;

import static com.atmosplayads.admobsample.MainActivity.INTERSTITIAL_ID;

public class InterstitialActivity extends Activity {

    private static final String TAG = "InterstitialActivity";

    View mProgressBar;
    TextView mLogView;
    private InterstitialAd mInterstitialAd;
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            addLog("onAdClosed");
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            addLog("onAdOpened");
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            addLog("onAdLeftApplication");
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            mProgressBar.setVisibility(View.GONE);
            addLog("onAdLoaded");
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            mProgressBar.setVisibility(View.GONE);
            addLog("onAdFailedToLoad errorCode:" + errorCode);
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
            addLog("onAdClicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        mProgressBar = findViewById(R.id.loading_bar);
        mLogView = findViewById(R.id.log_text);
        if (mInterstitialAd == null) {

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(INTERSTITIAL_ID);
            mInterstitialAd.setAdListener(adListener);
        }
    }


    public void loadAd(View view) {
        if(mInterstitialAd.isLoaded()){
           return;
        }
        mLogView.setText("");
        mProgressBar.setVisibility(View.VISIBLE);
        AdRequest req = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(req);
        addLog("start loading ad");
    }

    public void displayAd(View v) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            addLog("ad no loaded");
        }
    }

    void addLog(String msg) {
        Log.d(TAG, "AdMobDemo=> " + msg);
        mLogView.append("\n" + msg);
    }

}
