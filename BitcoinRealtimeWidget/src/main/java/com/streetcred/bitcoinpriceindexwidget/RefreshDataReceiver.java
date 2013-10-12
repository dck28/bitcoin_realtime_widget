package com.streetcred.bitcoinpriceindexwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by denniskong on 10/6/13.
 */
public class RefreshDataReceiver extends BroadcastReceiver{
    public static final String ACTION = "com.streetcred.bitcoinpriceindexwidget.REFRESH_REQUEST";
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            RefreshData refresh = new RefreshData();
            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.update_time, "* no connection");
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, XBTRealtimeWidgetProvider.class), remoteViews);
        } catch (Exception e){
            // ignore
        }
    }
}