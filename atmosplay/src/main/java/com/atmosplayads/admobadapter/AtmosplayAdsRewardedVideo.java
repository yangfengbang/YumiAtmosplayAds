package com.atmosplayads.admobadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.AtmosplayRewardVideo;
import com.atmosplayads.listener.AtmosplayAdListener;
import com.atmosplayads.listener.AtmosplayAdLoadListener;
import com.google.android.gms.ads.AdFormat;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.VersionInfo;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;

import java.util.List;

import static com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD;

/**
 * Description: Admob new Rewarded Custom Events
 * Created by yfb on 2019/12/18.
 */

public class AtmosplayAdsRewardedVideo extends Adapter implements MediationRewardedAd {
    private static final String TAG = "AtmosplayRewardedVideo";
    private AtmosplayRewardVideo mRewardVideo;
    private AtmosplayAdsUtil.AtmosplayParams params;
    private MediationRewardedAdCallback rewardedAdCallback;
    private boolean hasInitAtmosplaySDK = false;

    @Override
    public void initialize(Context context, InitializationCompleteCallback initializationCompleteCallback, List<MediationConfiguration> mediationConfigurations) {
        Log.d(TAG, "rewarded video initialize");
        if (!(context instanceof Activity)) {
            // Context not an Activity context, fail the initialization.
            initializationCompleteCallback.onInitializationFailed(
                    "init error: AtmosplayAds needs Activity object to initialize sdk.");
            return;
        }

        Bundle serverParameters = null;
        for (MediationConfiguration configuration : mediationConfigurations) {
            if (configuration.getFormat() == AdFormat.REWARDED) {
                serverParameters = configuration.getServerParameters();
            }
        }

        if (serverParameters == null) {
            initializationCompleteCallback.onInitializationFailed(
                    "init error: AtmosplayAds server parameters is null");
            return;
        }

        params = new AtmosplayAdsUtil.AtmosplayParams(serverParameters.getString(CUSTOM_EVENT_SERVER_PARAMETER_FIELD));

        if (TextUtils.equals(params.appId, "") || TextUtils.equals(params.unitId, "")) {
            initializationCompleteCallback.onInitializationFailed(
                    "init error: AtmosplayAds AppId or UnitId is null");
            return;
        }

        if (!hasInitAtmosplaySDK) {
            Log.d(TAG, "requestReadPhoneState: " + params.requestReadPhoneState);
            AtmosplayAdsSettings.enableAutoRequestPermissions(params.requestReadPhoneState);
            Log.d(TAG, "gdprState: " + params.gdprState);
            AtmosplayAdsUtil.setGDPRConsent(params.gdprState);

            mRewardVideo = AtmosplayRewardVideo.init(context, params.appId);
            mRewardVideo.setAutoLoadAd(params.autoLoad);
            mRewardVideo.setChannelId(params.channelId);

            hasInitAtmosplaySDK = true;
            initializationCompleteCallback.onInitializationSucceeded();
        }
    }

    @Override
    public VersionInfo getVersionInfo() {
        String versionString = AtmosplayAdsUtil.VERSION;
        String[] splits = versionString.split("\\.");
        int major = Integer.parseInt(splits[0]);
        int minor = Integer.parseInt(splits[1]);
        int micro = Integer.parseInt(splits[2]) * 100 + Integer.parseInt(splits[3]);
        return new VersionInfo(major, minor, micro);
    }

    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = AtmosplayAdsUtil.SDK_VERSION;
        String[] splits = versionString.split("\\.");
        int major = Integer.parseInt(splits[0]);
        int minor = Integer.parseInt(splits[1]);
        int micro = Integer.parseInt(splits[2]);
        return new VersionInfo(major, minor, micro);
    }


    @Override
    public void loadRewardedAd(MediationRewardedAdConfiguration configuration, final MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        Log.d(TAG, "requestRewardedVideoAd");
        Context context = configuration.getContext();

        Bundle serverParameters = configuration.getServerParameters();
        String serviceString = serverParameters.getString(MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        params = new AtmosplayAdsUtil.AtmosplayParams(serviceString);

        if (TextUtils.equals(params.appId, "") || TextUtils.equals(params.unitId, "")) {
            mediationAdLoadCallback.onFailure("Atmosplayads load failed, AtmosplayAds AppId or UnitId is null");
            return;
        }

        if (!hasInitAtmosplaySDK) {
            Log.d(TAG, "requestReadPhoneState: " + params.requestReadPhoneState);
            AtmosplayAdsSettings.enableAutoRequestPermissions(params.requestReadPhoneState);
            Log.d(TAG, "gdprState: " + params.gdprState);
            AtmosplayAdsUtil.setGDPRConsent(params.gdprState);

            hasInitAtmosplaySDK = true;
            mRewardVideo = AtmosplayRewardVideo.init(context, params.appId);
            mRewardVideo.setAutoLoadAd(params.autoLoad);
            mRewardVideo.setChannelId(params.channelId);

        }

        if (mRewardVideo != null) {
            mRewardVideo.loadAd(params.unitId, new AtmosplayAdLoadListener() {
                @Override
                public void onLoadFinished() {
                    Log.d(TAG, "onLoadFinished");
                    rewardedAdCallback = mediationAdLoadCallback.onSuccess(AtmosplayAdsRewardedVideo.this);
                }

                @Override
                public void onLoadFailed(int code, String errorMsg) {
                    Log.e(TAG, "onLoadFailed code: " + code + ", errorMsg: " + errorMsg);
                    mediationAdLoadCallback.onFailure("Atmosplayads load failed,error code: " + code + ", errorMsg: " + errorMsg);
                }
            });
        }
    }

    @Override
    public void showAd(Context context) {
        if (mRewardVideo.isReady(params.unitId)) {
            mRewardVideo.show(params.unitId, new AtmosplayAdListener() {

                @Override
                public void onVideoStart() {
                    Log.d(TAG, "onVideoStart");
                    rewardedAdCallback.onAdOpened();
                    rewardedAdCallback.onVideoStart();
                    rewardedAdCallback.reportAdImpression();
                }

                @Override
                public void onVideoFinished() {
                    Log.d(TAG, "onVideoFinished");
                    rewardedAdCallback.onVideoComplete();
                }

                @Override
                public void onUserEarnedReward() {
                    Log.d(TAG, "onUserEarnedReward");
                    rewardedAdCallback.onUserEarnedReward(null);
                }

                @Override
                public void onLandingPageInstallBtnClicked() {
                    Log.d(TAG, "onLandingPageInstallBtnClicked");
                    rewardedAdCallback.reportAdClicked();
                }

                @Override
                public void onAdClosed() {
                    Log.d(TAG, "onAdClosed");
                    rewardedAdCallback.onAdClosed();
                }

                public void onAdsError(int var1, String var2) {
                    Log.e(TAG, "present onAdsError code: " + var1 + ", errorMsg: " + var2);
                    rewardedAdCallback.onAdFailedToShow("show failed , errorCode:" + var1 + ", errorMsg: " + var2);
                }

            });
        } else {
            // Report that ad cannot be shown.
            rewardedAdCallback.onAdFailedToShow("Ad is unavailable to show.");
        }
    }
}
