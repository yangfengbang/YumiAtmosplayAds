package com.atmosplayads.admobadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.AtmosplayInterstitial;
import com.atmosplayads.listener.AtmosplayAdLoadListener;
import com.atmosplayads.listener.SimpleAtmosplayAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

public class AtmosplayAdsInterstitial implements CustomEventInterstitial {
    private static final String TAG = "AtmosplayInterstitial";
    private AtmosplayInterstitial mInterstitial;
    private CustomEventInterstitialListener mMediationInterstitialListener;
    private AtmosplayAdsUtil.AtmosplayParams params;

    @Override
    public void requestInterstitialAd(Context context, CustomEventInterstitialListener listener, String serverParameter, MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
        try {
            Log.e(TAG, "requestInterstitialAd");
            if (!(context instanceof Activity)) {
                Log.e(TAG, "init error: AtmosplayAds needs Activity object to initialize sdk.");
                listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
                return;
            }
            params = new AtmosplayAdsUtil.AtmosplayParams(serverParameter);

            Log.d(TAG, "requestReadPhoneState: " + params.requestReadPhoneState);
            AtmosplayAdsSettings.enableAutoRequestPermissions(params.requestReadPhoneState);
            Log.d(TAG, "gdprState: " + params.gdprState);
            AtmosplayAdsUtil.setGDPRConsent(params.gdprState);

            mInterstitial = AtmosplayInterstitial.init(context, params.appId);
            mInterstitial.setAutoLoadAd(params.autoLoad);
            mInterstitial.setChannelId(params.channelId);
            mMediationInterstitialListener = listener;
            mInterstitial.loadAd(params.unitId, new AtmosplayAdLoadListener() {
                @Override
                public void onLoadFinished() {
                    Log.d(TAG, "onLoadFinished");
                    mMediationInterstitialListener.onAdLoaded();
                }

                @Override
                public void onLoadFailed(int code, String errorMsg) {
                    Log.e(TAG, "onLoadFailed code: " + code + ", errorMsg: " + errorMsg);
                    mMediationInterstitialListener.onAdFailedToLoad(code);
                }
            });
        } catch (IllegalArgumentException e) {
            if (mMediationInterstitialListener != null) {
                mMediationInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            }
        }
    }

    @Override
    public void showInterstitial() {
        if (mInterstitial.isReady(params.unitId)) {
            mMediationInterstitialListener.onAdOpened();
            mInterstitial.show(params.unitId, new SimpleAtmosplayAdListener() {

                @Override
                public void onAdsError(int var1, String var2) {
                    Log.e(TAG, "present onAdsError code: " + var1 + ", errorMsg" + var2);
                    mMediationInterstitialListener.onAdFailedToLoad(var1);
                }

                @Override
                public void onVideoStart() {
                    Log.d(TAG, "onVideoStart");
                }

                @Override
                public void onAdClosed() {
                    Log.d(TAG, "onAdClosed");
                    mMediationInterstitialListener.onAdClosed();
                }

                @Override
                public void onLandingPageInstallBtnClicked() {
                    Log.d(TAG, "onInstallBtnClicked");
                    mMediationInterstitialListener.onAdClicked();
                }

            });
        }
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
