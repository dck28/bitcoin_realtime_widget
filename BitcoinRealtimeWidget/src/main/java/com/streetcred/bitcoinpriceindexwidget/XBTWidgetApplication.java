package com.streetcred.bitcoinpriceindexwidget;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.streetcred.bitcoinpriceindexwidget.Refresh.RefreshIntervalManager;

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
        new RefreshIntervalLauncherThread(getApplicationContext()).start();
    }

    public static SharedPreferences getSharedPreferences() {
        return instance.getSharedPreferences(XBTWidgetApplication.LOG_TAG, Context.MODE_PRIVATE);
    }

    private static class RefreshIntervalLauncherThread extends Thread {
        private Context context;

        private RefreshIntervalLauncherThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            Context context = this.context;
            this.context = null;
            RefreshIntervalManager.instance().startIntervalRefreshing(context, true);
        }
    }
}