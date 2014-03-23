package com.streetcred.bitcoinpriceindexwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.util.concurrent.TimeUnit;

/**
 * Created by StreetCred Inc. on 10/5/13.
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
            remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, context);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }

        // Set app_icon clickable
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.app_icon, configPendingIntent);

        // Set exchange currency display
        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
                .equalsIgnoreCase("中文(繁體)")){
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChinese(
                        XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")
                    ));
            if(Util.convertCurrencyStringToChinese(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民幣")
                    || Util.convertCurrencyStringToChinese(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台幣")){
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
            } else {
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            }
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk") +
                    " 提供報價");
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
                .equalsIgnoreCase("中文(简体)")){
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChineseSimplified(
                        XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")
                    ));
            if(Util.convertCurrencyStringToChineseSimplified(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民币")
                    || Util.convertCurrencyStringToChineseSimplified(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台币")){
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
            } else {
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            }
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk") +
                    " 提供报价");
        } else {
            remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            remoteViews.setTextViewText(R.id.exchange_currency, XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            remoteViews.setTextViewText(R.id.credit, "Source: " +
                    XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
        }

        // Set widget textview refreshable
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.price, refreshPendingIntent);

        // Set widget theme
        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Float").equalsIgnoreCase("Navy")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor("#DD2B3856"));
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Float").equalsIgnoreCase("Clementine")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor("#DDEB5E00"));
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Float").equalsIgnoreCase("Float")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.TRANSPARENT);
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_LAST_UPDATED_THEME, "Float").equalsIgnoreCase("Glassy")){
            remoteViews.setInt(R.id.background, "setBackgroundColor", Color.parseColor("#AA383838"));
        }
        remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, context);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}