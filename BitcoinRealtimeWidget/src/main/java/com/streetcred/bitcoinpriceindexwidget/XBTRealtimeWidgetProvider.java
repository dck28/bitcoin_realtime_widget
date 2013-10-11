package com.streetcred.bitcoinpriceindexwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by denniskong on 10/5/13.
 */
public class XBTRealtimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get quote
        RefreshData refresh = new RefreshData();
        refresh.execute();

        // Set app_icon clickable
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.app_icon, configPendingIntent);
        remoteViews.setTextViewText(R.id.exchange_currency, XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        // Set widget textview refreshable
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.price, refreshPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}