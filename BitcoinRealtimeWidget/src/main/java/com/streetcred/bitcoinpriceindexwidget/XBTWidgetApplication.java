package com.streetcred.bitcoinpriceindexwidget;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.streetcred.bitcoinpriceindexwidget.Notify.Alert;
import com.streetcred.bitcoinpriceindexwidget.Refresh.RefreshIntervalManager;
import com.streetcred.bitcoinpriceindexwidget.Utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by StreetCred Inc. on 10/5/13.
 */
public class XBTWidgetApplication extends Application {

    public static final String LOG_TAG = XBTWidgetApplication.class.getSimpleName();
    public static XBTWidgetApplication instance;
    public static ArrayList<Alert> listOfAlerts;

    public XBTWidgetApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new RefreshIntervalLauncherThread(getApplicationContext()).start();
        if (null == listOfAlerts) {
            listOfAlerts = new ArrayList<Alert>();
        }

        //      load tasks from preference
        SharedPreferences prefs = getSharedPreferences();
        try {
            listOfAlerts = (ArrayList<Alert>) ObjectSerializer.deserialize(prefs.getString(Constants.PREF_STORAGE_FOR_LIST_OF_ALERTS, ObjectSerializer.serialize(new ArrayList<Alert>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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