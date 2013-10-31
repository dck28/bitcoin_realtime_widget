package com.streetcred.bitcoinpriceindexwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/5/13.
 */
public class XBTRealtimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get quote
        try{
            RefreshData refresh = new RefreshData();
            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.update_time, "* no connection");
            remoteViews.setTextColor(R.id.price, Color.GRAY);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }

        // Set app_icon clickable
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.app_icon, configPendingIntent);

        // Set exchange currency display
        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
                .equalsIgnoreCase("中文(繁體)")){
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChinese(
                        XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")
                    ));
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk") +
                    " 提供報價");
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
                .equalsIgnoreCase("中文(简体)")){
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChineseSimplified(
                        XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")
                    ));
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk") +
                    " 提供报价");
        } else {
            remoteViews.setTextViewText(R.id.exchange_currency, XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            remoteViews.setTextViewText(R.id.credit, "Data provided by " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
        }

        // Set widget textview refreshable
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.price, refreshPendingIntent);

        // Set widget theme
        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Navy").equalsIgnoreCase("Navy")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor("#DD2B3856"));
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Navy").equalsIgnoreCase("Float")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.TRANSPARENT);
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Navy").equalsIgnoreCase("Frost")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor("#AAF0FFFC"));
        }
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}