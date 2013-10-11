package com.streetcred.bitcoinpriceindexwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

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
        remoteViews.setTextViewText(R.id.update_time, "* loading...");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            newPrice = getNewPrice();
            if (newPrice != 0){
                DecimalFormat df = new DecimalFormat("0.0");
                remoteViews.setTextViewText(R.id.price, df.format(newPrice));
                String time_now_in_string = Util.getCurrentDisplayTime();
                remoteViews.setTextViewText(R.id.update_time, "* updated on " + time_now_in_string);
                remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
                pref.edit()
                        .putLong(Constants.PREF_LAST_UPDATED_TIMESTAMP, System.currentTimeMillis())
                        .putString(Constants.PREF_LAST_UPDATED_PRICE, df.format(newPrice))
                        .commit();
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                Log.e("updated widget?", "yes");
            } else {
                remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "--.--"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.update_time, "* no connection");
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    private double getNewPrice(){
        try{
            String stringResponse = null;
            String USD_code = null; String USD_symbol = null; String USD_rate = null;
            String USD_description = null; String USD_rate_float = null;
            String GBP_code = null; String GBP_symbol = null; String GBP_rate = null;
            String GBP_description = null; String GBP_rate_float = null;
            String EUR_code = null; String EUR_symbol = null; String EUR_rate = null;
            String EUR_description = null; String EUR_rate_float = null;

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(Constants.API_URL);
            HttpResponse response =  client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            stringResponse = builder.toString();
            JSONTokener tokener = new JSONTokener(stringResponse);
            JSONObject json_response = new JSONObject(tokener);
            JSONObject bpi = json_response.optJSONObject("bpi");

            // FETCH USD PRICE
            JSONObject USD = bpi.optJSONObject("USD");

            if(USD.has("code") && !USD.isNull("code")){
                USD_code = USD.optString("code");
            }
            if(USD.has("symbol") && !USD.isNull("symbol")){
                USD_symbol = USD.optString("symbol");
            }
            if(USD.has("rate") && !USD.isNull("rate")){
                USD_rate = USD.optString("rate");
            }
            if(USD.has("description") && !USD.isNull("description")){
                USD_description = USD.optString("description");
            }
            if(USD.has("rate_float") && !USD.isNull("rate_float")){
                USD_rate_float = USD.optString("rate_float");
            }
            if(USD_rate != null && pref.getString("preferred_currency", "USD").equalsIgnoreCase("USD")) return Double.parseDouble(USD_rate);

            // FETCH GBP PRICE
            JSONObject GBP = bpi.optJSONObject("GBP");

            if(GBP.has("code") && !GBP.isNull("code")){
                GBP_code = GBP.optString("code");
            }
            if(GBP.has("symbol") && !GBP.isNull("symbol")){
                GBP_symbol = GBP.optString("symbol");
            }
            if(GBP.has("rate") && !GBP.isNull("rate")){
                GBP_rate = GBP.optString("rate");
            }
            if(GBP.has("description") && !GBP.isNull("description")){
                GBP_description = GBP.optString("description");
            }
            if(GBP.has("rate_float") && !GBP.isNull("rate_float")){
                GBP_rate_float = GBP.optString("rate_float");
            }
            if(GBP_rate != null && pref.getString("preferred_currency", "USD").equalsIgnoreCase("GBP")) return Double.parseDouble(GBP_rate);

            // FETCH EUR PRICE
            JSONObject EUR = bpi.optJSONObject("EUR");

            if(EUR.has("code") && !EUR.isNull("code")){
                EUR_code = EUR.optString("code");
            }
            if(EUR.has("symbol") && !EUR.isNull("symbol")){
                EUR_symbol = EUR.optString("symbol");
            }
            if(EUR.has("rate") && !EUR.isNull("rate")){
                EUR_rate = EUR.optString("rate");
            }
            if(EUR.has("description") && !EUR.isNull("description")){
                EUR_description = EUR.optString("description");
            }
            if(EUR.has("rate_float") && !EUR.isNull("rate_float")){
                EUR_rate_float = EUR.optString("rate_float");
            }
            if(EUR_rate != null && pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("EUR")) return Double.parseDouble(EUR_rate);
        } catch (Exception ex){
            ex.printStackTrace();
            //ignore
        }
        return 0.00;
    }
}
