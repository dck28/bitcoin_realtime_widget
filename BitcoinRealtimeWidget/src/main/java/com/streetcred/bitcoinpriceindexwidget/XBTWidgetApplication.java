package com.streetcred.bitcoinpriceindexwidget;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by denniskong on 10/5/13.
 */
public class XBTWidgetApplication extends Application {

    public static final String LOG_TAG = XBTWidgetApplication.class.getSimpleName();
    public static XBTWidgetApplication instance;

    public XBTWidgetApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static SharedPreferences getSharedPreferences() {
        return instance.getSharedPreferences(XBTWidgetApplication.LOG_TAG, Context.MODE_PRIVATE);
    }
}