package com.atmosplayads.admobadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.AtmosplayRewardVideo;
import com.atmosplayads.listener.AtmosplayAdListener;
import com.atmosplayads.listener.AtmosplayAdLoadListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdListener;

/**
 * Description: Admob old RewardedVideo Custom Events
 * Created by yfb on 2019/12/18.
 */

@SuppressWarnings("unused")
public class AtmosplayAdsRewardedVideoLegacy implements MediationRewardedVideoAdAdapter {
    private static final String TAG = "AtmosplayRewardedLegacy";
    private AtmosplayRewardVideo mRewardVideo;
    private MediationRewardedVideoAdListener mRewardedVideoEventForwarder;
    private AtmosplayAdsUtil.AtmosplayParams params;

    @Override
    public void initialize(Context context, MediationAdRequest mediationAdRequest, String s, MediationRewardedVideoAdListener mediationRewardedVideoAdListener, Bundle serverParameters, Bundle bundle1) {
        Log.d(TAG, "rewarded video initialize");
        if (!(context instanceof Activity)) {
            Log.e(TAG, "init error: AtmosplayAds needs Activity object to initialize sdk.");
            mediationRewardedVideoAdListener.onAdFailedToLoad(this, AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        params = new AtmosplayAdsUtil.AtmosplayParams(serverParameters.getString(CUSTOM_EVENT_SERVER_PARAMETER_FIELD));

        Log.d(TAG, "requestReadPhoneState: " + params.requestReadPhoneState);
        AtmosplayAdsSettings.enableAutoRequestPermissions(params.requestReadPhoneState);
        Log.d(TAG, "gdprState: " + params.gdprState);
        AtmosplayAdsUtil.setGDPRConsent(params.gdprState);

        mRewardVideo = AtmosplayRewardVideo.init(context, params.appId);
        mRewardVideo.setAutoLoadAd(params.autoLoad);
        mRewardVideo.setChannelId(params.channelId);
        mRewardedVideoEventForwarder = mediationRewardedVideoAdListener;
        mRewardedVideoEventForwarder.onInitializationSucceeded(AtmosplayAdsRewardedVideoLegacy.this);
    }

    @Override
    public void loadAd(MediationAdRequest mediationAdRequest, Bundle serverParameters, Bundle bundle1) {
        if (mRewardVideo == null) {
            Log.e(TAG, "AtmosplayAds not initialized.");
            return;
        }
        loadAd();
    }

    private void loadAd() {
        Log.d(TAG, "loadAdRewardedVideoAd");
        mRewardVideo.loadAd(params.unitId, new AtmosplayAdLoadListener() {
            @Override
            public void onLoadFinished() {
                Log.d(TAG, "onLoadFinished");
                mRewardedVideoEventForwarder.onAdLoaded(AtmosplayAdsRewardedVideoLegacy.this);
            }

            @Override
            public void onLoadFailed(int code, String errorMsg) {
                Log.e(TAG, "onLoadFailed code: " + code + ", errorMsg: " + errorMsg);
                mRewardedVideoEventForwarder.onAdFailedToLoad(AtmosplayAdsRewardedVideoLegacy.this, 0);
            }
        });
    }

    @Override
    public void showVideo() {
        if (mRewardVideo.isReady(params.unitId)) {
            mRewardedVideoEventForwarder.onAdOpened(AtmosplayAdsRewardedVideoLegacy.this);
            mRewardVideo.show(params.unitId, new AtmosplayAdListener() {

                @Override
                public void onVideoStart() {
                    Log.d(TAG, "onVideoStart");
                    mRewardedVideoEventForwarder.onVideoStarted(AtmosplayAdsRewardedVideoLegacy.this);
                }

                @Override
                public void onVideoFinished() {
                    Log.d(TAG, "onVideoFinished");
                    mRewardedVideoEventForwarder.onVideoCompleted(AtmosplayAdsRewardedVideoLegacy.this);
                }

                @Override
                public void onUserEarnedReward() {
                    Log.d(TAG, "onUserEarnedReward");
                    mRewardedVideoEventForwarder.onRewarded(AtmosplayAdsRewardedVideoLegacy.this, null);
                }

                @Override
                public void onLandingPageInstallBtnClicked() {
                    Log.d(TAG, "onLandingPageInstallBtnClicked");
                    mRewardedVideoEventForwarder.onAdClicked(AtmosplayAdsRewardedVideoLegacy.this);
                }

                @Override
                public void onAdClosed() {
                    Log.d(TAG, "onAdClosed");
                    mRewardedVideoEventForwarder.onAdClosed(AtmosplayAdsRewardedVideoLegacy.this);
                }

                public void onAdsError(int var1, String var2) {
                    Log.e(TAG, "present onAdsError code: " + var1 + ", errorMsg: " + var2);
                    mRewardedVideoEventForwarder.onAdFailedToLoad(AtmosplayAdsRewardedVideoLegacy.this, 0);
                }

            });
        }
    }

    @Override
    public boolean isInitialized() {
        return mRewardVideo != null;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }
}
