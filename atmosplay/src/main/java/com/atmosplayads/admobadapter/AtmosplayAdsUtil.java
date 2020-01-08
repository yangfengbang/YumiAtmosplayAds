package com.atmosplayads.admobadapter;

import android.text.TextUtils;
import android.util.Log;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.entity.GDPRStatus;

import org.json.JSONException;
import org.json.JSONObject;

import static android.text.TextUtils.isEmpty;

class AtmosplayAdsUtil {
    private static final String TAG = "AtmosplayAdsUtil";
    static final String VERSION = "3.0.0.0";
    static final String SDK_VERSION = "3.0.0";

    static void setGDPRConsent(String gdprConsentState) {
        if (TextUtils.equals("NON_PERSONALIZED", gdprConsentState)) {
            AtmosplayAdsSettings.setGDPRConsent(GDPRStatus.NON_PERSONALIZED);
        } else if (TextUtils.equals("PERSONALIZED", gdprConsentState)) {
            AtmosplayAdsSettings.setGDPRConsent(GDPRStatus.PERSONALIZED);
        } else {
            AtmosplayAdsSettings.setGDPRConsent(GDPRStatus.UNKNOWN);
        }

    }

    static class AtmosplayParams {
        String appId = "";
        String unitId = "";
        boolean autoLoad;
        String channelId = "";
        boolean requestReadPhoneState = false;
        String gdprState = "";

        AtmosplayParams(String json) {
            if (isEmpty(json)) {
                return;
            }

            try {
                JSONObject jo = new JSONObject(json);
                appId = getString(jo, "appId");
                unitId = getString(jo, "unitId");
                channelId = getString(jo, "channelId");
                autoLoad = getBoolean(jo, "autoLoad");
                requestReadPhoneState = getBoolean(jo, "requestReadPhoneState");
                gdprState = getString(jo, "gdprState");
            } catch (JSONException e) {
                Log.d(TAG, "YumiParams: parse error, ", e);
            }
        }

        private String getString(JSONObject jo, String key) {
            try {
                return jo.getString(key);
            } catch (Exception e) {
                Log.d(TAG, "YumiParams: parse error, ", e);
                return "";
            }
        }

        private boolean getBoolean(JSONObject jo, String key) {
            try {
                return jo.getBoolean(key);
            } catch (Exception e) {
                Log.d(TAG, "YumiParams: parse error, ", e);
                return false;
            }
        }

    }
}
