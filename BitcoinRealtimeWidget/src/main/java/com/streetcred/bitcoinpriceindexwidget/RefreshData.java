package com.streetcred.bitcoinpriceindexwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.streetcred.bitcoinpriceindexwidget.ConnectionManager.JSONParser;
import com.streetcred.bitcoinpriceindexwidget.ConnectionManager.RpcManager;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by denniskong on 10/6/13.
 */

public class RefreshData extends AsyncTask<String, Void, String> {

    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;
    double newPrice;
    SharedPreferences pref;
    Context context;

    @Override
    protected void onPreExecute() {
        this.context = XBTWidgetApplication.instance;
        this.appWidgetManager = AppWidgetManager.getInstance(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        thisWidget = new ComponentName(context, XBTRealtimeWidgetProvider.class);
        pref = XBTWidgetApplication.getSharedPreferences();
        remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "--.--"));
        remoteViews.setTextColor(R.id.price, Color.parseColor("#FFFFB2"));
        if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
            remoteViews.setTextViewText(R.id.update_time, "* 連接中...");
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                    + " 提供報價");
        } else {
            remoteViews.setTextViewText(R.id.update_time, "* loading...");
            remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            remoteViews.setTextViewText(R.id.credit, "Data provided by " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String data_source = pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk");

            String data_source_url = getURL_from_source(data_source);

            Collection<BasicNameValuePair> requestParams = null;
            if (data_source_url.equals(Constants.COINBASE_API_URL)){
                requestParams = new ArrayList<BasicNameValuePair>();
                requestParams.add(new BasicNameValuePair("currency", pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
            } else if (data_source_url.equals(Constants.MTGOX_API_BASEURL)){
                data_source_url = data_source_url + "/BTC" + pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD") + "/money/ticker_fast";
            }

            JSONObject json_response = RpcManager.getInstance().callGet(context, data_source_url, "", requestParams);
            newPrice = getNewPrice(json_response, data_source);
            if (newPrice != 0){
                DecimalFormat df = new DecimalFormat("0.0");
                remoteViews.setTextViewText(R.id.price, df.format(newPrice));
                String time_now_in_string = Util.getCurrentDisplayTime();
                if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 更新時間 " + time_now_in_string);
                    remoteViews.setTextColor(R.id.price, Color.WHITE);
                } else {
                    remoteViews.setTextViewText(R.id.update_time, "* updated on " + time_now_in_string);
                    remoteViews.setTextColor(R.id.price, Color.WHITE);
                }
                pref.edit()
                        .putLong(Constants.PREF_LAST_UPDATED_TIMESTAMP, System.currentTimeMillis())
                        .putString(Constants.PREF_LAST_UPDATED_PRICE, df.format(newPrice))
                        .commit();
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                Log.e("updated widget?", "yes");
            } else {
                // Update widget info when no connection
                remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "--.--"));
                if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    remoteViews.setTextViewText(R.id.exchange_currency,
                            Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
                    remoteViews.setTextViewText(R.id.credit, "由 "
                            + pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                            + " 提供報價");
                } else {
                    remoteViews.setTextViewText(R.id.update_time, "* no connection");
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
                    remoteViews.setTextViewText(R.id.credit, "Data provided by " +
                            pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
                }
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            }
        } catch (Exception e) {
            e.printStackTrace();
            remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "--.--"));
            if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                remoteViews.setTextViewText(R.id.exchange_currency,
                        Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
            } else {
                remoteViews.setTextViewText(R.id.update_time, "* no connection");
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            }
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    private double getNewPrice(JSONObject json_response, String from){

        if (from.equals("Coindesk")){
            return JSONParser.handle_source_COINDESK(json_response, pref);
        }

        if (from.equals("Coinbase")){
            return JSONParser.handle_source_COINBASE(json_response);
        }

        if (from.equals("Mt. Gox")){
            return JSONParser.handle_source_MTGOX(json_response);
        }

        // Default
        return 0.00;
    }

    private String getURL_from_source(String data_source){
        if(data_source.equalsIgnoreCase("Coindesk")){
            return Constants.COINDESK_API_URL;
        } else if(data_source.equalsIgnoreCase("Coinbase")){
            return Constants.COINBASE_API_URL;
        } else if(data_source.equalsIgnoreCase("Mt. Gox")){
            return Constants.MTGOX_API_BASEURL;
        }
        //Default
        return Constants.COINDESK_API_URL;
    }

}
