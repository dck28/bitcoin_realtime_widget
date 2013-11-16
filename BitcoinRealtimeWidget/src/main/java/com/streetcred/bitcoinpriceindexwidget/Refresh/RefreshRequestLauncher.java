package com.streetcred.bitcoinpriceindexwidget.Refresh;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
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

import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 11/11/13.
 */
public class RefreshRequestLauncher extends Thread {
    private PowerManager.WakeLock wakeLock;
    private Context context;
    private Handler handler;
    private RemoteViews remoteViews;
    private double previous_price;

    protected RefreshRequestLauncher(PowerManager.WakeLock wakeLock,
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
                Log.e("RefreshIntervalReceiver", "Started");
                RefreshData refresh = new RefreshData();
                refresh.execute().get(10000, TimeUnit.MILLISECONDS);
            } catch (Exception e){
                remoteViews.setTextViewText(R.id.update_time, "* no connection");
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                Log.e("RefreshIntervalReceiver","Refresh Incomplete / Exception Caught");
            } finally {
                if (didNotReceiveValidNewPrice()){
                    // handle if new price not available
                } else {
                    applyTextColoring(context, remoteViews, previous_price);
                }
                Log.e("RefreshIntervalReceiver","Refresh Completed / Successful");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                if(appWidgetManager != null){
                    appWidgetManager.updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
                }
            }
        } finally {
            PowerLockProvider.release(wakeLock, handler);
            Log.e("RefreshIntervalReceiver PowerLock","released");
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