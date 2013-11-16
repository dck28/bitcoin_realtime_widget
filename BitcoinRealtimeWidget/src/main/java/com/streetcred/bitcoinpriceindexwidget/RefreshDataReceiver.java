package com.streetcred.bitcoinpriceindexwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/6/13.
 */
public class RefreshDataReceiver extends BroadcastReceiver{
    public static final String ACTION = "com.streetcred.bitcoinpriceindexwidget.REFRESH_REQUEST";
    public static final String ACTION_UPDATE_WITH_TICKER = "com.streetcred.bitcoinpriceindexwidget.REFRESH_REQUEST_WITH_TICKER";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_UPDATE_WITH_TICKER)){
            XBTWidgetApplication.getSharedPreferences().edit().putBoolean(Constants.PREF_IS_FROM_ONGOING_NOTIFICATION, true).commit();
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        double previous_price = Double.parseDouble(
                XBTWidgetApplication
                        .getSharedPreferences()
                        .getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0")
                        .replace(",","."));
                        // Fix for invalid parseDouble when Data from API is malformed: e.g. ""
                        // Nov 8, 2013 : java.lang.NumberFormatException
        try{
            RefreshData refresh = new RefreshData();
            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            remoteViews.setTextViewText(R.id.update_time, "* no connection");
            remoteViews.setTextColor(R.id.price, Color.GRAY);
        } finally {
            if (didNotReceiveValidNewPrice()){
                // handle if new price not available
            } else {
                applyTextColoring(context, remoteViews, previous_price);
            }
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(appWidgetManager != null){
            appWidgetManager.updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
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