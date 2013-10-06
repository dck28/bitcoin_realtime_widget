package com.streetcred.bitcoinrealtimewidget;

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
    protected String doInBackground(String... params) {
        try {
            newPrice = getNewPrice();
            DecimalFormat df = new DecimalFormat("0.0");
            remoteViews.setTextViewText(R.id.price, df.format(newPrice));
            remoteViews.setTextViewText(R.id.update_time, "* updated just now");
            pref.edit().putLong(Constants.LAST_UPDATED_TIMESTAMP, System.currentTimeMillis()).commit();
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            Log.e("updated widget?", "yes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

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
    protected void onProgressUpdate(Void... values) {
    }

    private double getNewPrice(){
        try{
            String stringResponse = null;
            String USD_code = null;
            String USD_symbol = null;
            String USD_rate = null;
            String USD_description = null;
            String USD_rate_float = null;

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
            if(USD_rate != null) return Double.parseDouble(USD_rate);
        } catch (Exception ex){
            ex.printStackTrace();
            //ignore
        }
        return 0.00;
    }
}
