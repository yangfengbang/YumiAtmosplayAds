package com.atmosplayads.admobadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.AtmosplayBanner;
import com.atmosplayads.entity.BannerSize;
import com.atmosplayads.listener.BannerListener;
import com.atmosplayads.presenter.widget.AtmosBannerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

/**
 * Description:
 * <p>
 * Created by yfb on 2019-12-18.
 */
public class AtmosplayAdsBanner implements CustomEventBanner {
    private static final String TAG = "AtmosplayAdsBanner";

    private AtmosplayBanner mBanner;
    private Handler mHandler = new Handler(Looper.myLooper());
    private Context mContext;

    @Override
    public void requestBannerAd(Context context, final CustomEventBannerListener customEventBannerListener, String s, AdSize adSize, MediationAdRequest mediationAdRequest, Bundle bundle) {
        Log.d(TAG, "requestBannerAd");
        if (!(context instanceof Activity)) {
            Log.e(TAG, "init error: AtmosplayAds needs Activity object to initialize sdk.");
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        mContext = context;

        AtmosplayAdsUtil.AtmosplayParams params = new AtmosplayAdsUtil.AtmosplayParams(s);

        Log.d(TAG, "requestReadPhoneState: " + params.requestReadPhoneState);
        AtmosplayAdsSettings.enableAutoRequestPermissions(params.requestReadPhoneState);
        Log.d(TAG, "gdprState: " + params.gdprState);
        AtmosplayAdsUtil.setGDPRConsent(params.gdprState);

        mBanner = new AtmosplayBanner(context, params.appId, params.unitId);
        mBanner.setBannerSize(calculateAdSize(adSize));
        mBanner.setBannerContainer(null);
        mBanner.setChannelId(params.channelId);
        mBanner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerPrepared(AtmosBannerView view) {
                Log.d(TAG, "onBannerPrepared");
                customEventBannerListener.onAdLoaded(view);
            }

            @Override
            public void onBannerPreparedFailed(int code, String error) {
                Log.d(TAG, "onBannerPreparedFailed code: " + code + " ,errorMsg: " + error);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                       onDestroy();
                    }
                }, 9000);
                customEventBannerListener.onAdFailedToLoad(code);
            }

            @Override
            public void onBannerClicked() {
                Log.d(TAG, "onBannerClicked");
                customEventBannerListener.onAdClicked();
            }
        });
        mBanner.loadAd();
    }

    private BannerSize calculateAdSize(AdSize adSize) {
        // Use the smallest AdSize that will properly contain the adView
        if (adSize == AdSize.BANNER) {
            return BannerSize.BANNER_320x50;
        } else if (adSize == AdSize.LARGE_BANNER) {
            return BannerSize.BANNER_728x90;
        }
        return BannerSize.BANNER_320x50;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBanner != null) {
                    mBanner.destroy();
                    mBanner = null;
                }
            }
        });
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
    }
}