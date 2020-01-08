package com.atmosplayads.admobsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.atmosplayads.atmosplayadsadmobdemo.R;

import static com.atmosplayads.admobsample.MainActivity.NEW_VIDEO_ID;

public class RewardedVideoActivity extends Activity{
    private static final String TAG = "NewRewardedVideo";

    View mProgressBar;
    TextView mLogView;
    private RewardedAd mRewardedVideoAd;
    private RewardedAdLoadCallback mLoadAdCallback;
    private RewardedAdCallback adCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_video);

        mProgressBar = findViewById(R.id.loading_bar);
        mLogView = findViewById(R.id.log_text);

        mLoadAdCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                mProgressBar.setVisibility(View.GONE);
                addLog("onRewardedVideoAdLoaded");
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                mProgressBar.setVisibility(View.GONE);
                addLog("onRewardedVideoAdFailedToLoad error code: " + errorCode);
            }
        };

        adCallback = new RewardedAdCallback() {
            public void onRewardedAdOpened() {
                addLog("onRewardedVideoAdOpened");
            }

            public void onRewardedAdClosed() {
                addLog("onRewardedVideoAdClosed");
            }

            @Override
            public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                addLog("onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                        rewardItem.getAmount());
            }

            public void onRewardedAdFailedToShow(int errorCode) {
                addLog("onRewardedAdFailedToShow");
            }
        };
    }

    public void loadAd(View view) {
        mLogView.setText("");
        mProgressBar.setVisibility(View.VISIBLE);
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = new RewardedAd(this,
                NEW_VIDEO_ID);

        AdRequest request = new AdRequest.Builder().build();
        mRewardedVideoAd.loadAd(request, mLoadAdCallback);

        addLog("start loading ad");
    }

    public void displayAd(View v) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show(this, adCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void addLog(String msg) {
        Log.d(TAG, "AdMobDemo=> " + msg);
        mLogView.append("\n" + msg);
    }
}

