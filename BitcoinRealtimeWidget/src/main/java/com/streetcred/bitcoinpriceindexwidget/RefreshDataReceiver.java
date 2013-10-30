package com.streetcred.bitcoinpriceindexwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by denniskong on 10/6/13.
 */
public class RefreshDataReceiver extends BroadcastReceiver{
    public static final String ACTION = "com.streetcred.bitcoinpriceindexwidget.REFRESH_REQUEST";
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        double previous_price = Double.parseDouble(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0"));
        try{
            RefreshData refresh = new RefreshData();
            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e){
            remoteViews.setTextViewText(R.id.update_time, "* no connection");
            remoteViews.setTextColor(R.id.price, Color.GRAY);
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
        } catch (Exception e){
            // ignore
        } finally {
            double new_price = Double.parseDouble(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0"));
            if (new_price - previous_price >= 0.1){
                remoteViews.setTextColor(R.id.price, Color.parseColor("#CCE5CC"));
            } else if (new_price - previous_price <= -0.1){
                remoteViews.setTextColor(R.id.price, Color.parseColor("#FFCCCC"));
            } else {
                remoteViews.setTextColor(R.id.price, Color.WHITE);
            }
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
        }
    }
}