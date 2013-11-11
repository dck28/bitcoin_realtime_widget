package com.streetcred.bitcoinpriceindexwidget.Refresh;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.R;
import com.streetcred.bitcoinpriceindexwidget.RefreshData;
import com.streetcred.bitcoinpriceindexwidget.XBTRealtimeWidgetProvider;
import com.streetcred.bitcoinpriceindexwidget.XBTWidgetApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by denniskong on 11/8/13.
 */

public class RefreshIntervalReceiver extends BroadcastReceiver {
    private static final String LOCK_NAME = "lock." + RefreshIntervalReceiver.class.getName();
    private static final String LOG_TAG = RefreshIntervalReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) Log.e("Screen Unlock","detected");
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()) && wasLastUpdateSuccessful()){
            return;
        }
        Log.e("RefreshIntervalReceiver", "Starting Refresh");

        Handler handler = new Handler();
        PowerManager.WakeLock lock = PowerLockProvider.acquireLock(context, LOCK_NAME, 10000l, handler);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        double previous_price = Double.parseDouble(
                XBTWidgetApplication
                        .getSharedPreferences()
                        .getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0")
                        .replace(",","."));
        // Fix for invalid parseDouble when Data from API is malformed: e.g. ""
        // Nov 8, 2013 : java.lang.NumberFormatException


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(
                new RefreshRequestLauncher(
                        lock,
                        context,
                        handler,
                        remoteViews,
                        previous_price));

        try{
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean wasLastUpdateSuccessful(){
        return XBTWidgetApplication.getSharedPreferences()
                .getBoolean(Constants.WAS_LAST_UPDATE_SUCCESSFUL, true);
    }

    private static class RefreshRequestLauncher extends Thread {
        private PowerManager.WakeLock wakeLock;
        private Context context;
        private Handler handler;
        private RemoteViews remoteViews;
        private double previous_price;

        private RefreshRequestLauncher(PowerManager.WakeLock wakeLock,
                                       Context context,
                                       Handler handler,
                                       RemoteViews remoteViews,
                                       double previous_price) {
            this.wakeLock = wakeLock;
            this.context = context;
            this.handler = handler;
            this.remoteViews = remoteViews;
            this.previous_price = previous_price;
        }

        @Override
        public void run() {
            try{
                try{
                    RefreshData refresh = new RefreshData();
                    refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                    Log.e("Refresh","Started");
                } catch (Exception e){
                    remoteViews.setTextViewText(R.id.update_time, "* no connection");
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    Log.e("Refresh Incomplete","Exception Caught");
                } finally {
                    if (didNotReceiveValidNewPrice()){
                        // handle if new price not available
                    } else {
                        applyTextColoring(context, remoteViews, previous_price);
                    }
                    Log.e("Refresh Completed","Successful");
                    AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
                }
            } finally {
                PowerLockProvider.release(wakeLock, handler);
                Log.e("PowerLock","released");
            }

        }

        private boolean didNotReceiveValidNewPrice(){
            return !XBTWidgetApplication.getSharedPreferences().getBoolean(Constants.RECEIVED_VALID_NEW_PRICE, true);
        }

        private void applyTextColoring(Context context, RemoteViews remoteViews, double previous_price){
            double new_price = Double.parseDouble(
                    XBTWidgetApplication
                            .getSharedPreferences()
                            .getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0")
                            .replace(",","."));
            // Fix for invalid parseDouble when Data from API is malformed: e.g. ""
            // Nov 8, 2013 : java.lang.NumberFormatException
            if (new_price - previous_price >= 0.1){
                remoteViews.setTextColor(R.id.price, Color.parseColor("#CCE5CC"));
            } else if (new_price - previous_price <= -0.1){
                remoteViews.setTextColor(R.id.price, Color.parseColor("#FFCCCC"));
            } else {
                remoteViews.setTextColor(R.id.price, Color.WHITE);
            }
        }
    }
}
