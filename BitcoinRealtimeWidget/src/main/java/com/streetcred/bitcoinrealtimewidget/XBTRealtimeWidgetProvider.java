package com.streetcred.bitcoinrealtimewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/5/13.
 */
public class XBTRealtimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimedAPICall(context, appWidgetManager), 1, 30000);

        // Set app_icon clickable
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent configIntent = new Intent(context, MainActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.app_icon, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        // Set widget textview refreshable
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.price, refreshPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    private class TimedAPICall extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        double newPrice;
        SharedPreferences pref;
        long freq_pref_converted_from_string_interval;
        Context context;

        public TimedAPICall(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            thisWidget = new ComponentName(context, XBTRealtimeWidgetProvider.class);
            pref = XBTWidgetApplication.getSharedPreferences();
            this.context = context;
        }

        @Override
        public void run() {
            Log.e("called?", "yes");
            if (appWidgetManager!=null)Log.e("appmanager", "not null");
            else Log.e("appmanager", "is null");
            if (remoteViews!=null)Log.e("remoteViews", "not null");
            else Log.e("remoteViews", "is null");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String preferred_interval = sharedPref.getString("pref_freq", "4 minutes");
            freq_pref_converted_from_string_interval = Util.convertStringIntervalToLong(preferred_interval);
            try{
                Long updatedSince = System.currentTimeMillis() - pref.getLong(Constants.LAST_UPDATED_TIMESTAMP, 0);
                String converted_time_passed_to_string = convertTimePassedToString(updatedSince);
                Log.e("updatedSince", Long.toString(updatedSince));
                if(updatedSince > freq_pref_converted_from_string_interval){
                    Log.e("fetching","fetching");
                    final Thread t = new Thread() {
                        @Override
                        public void run() {
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
                        }
                    };
                    t.start();
                } else {
                    Log.e("updatedstring","updatedstring only");
                    if (updatedSince < 11000){
                        remoteViews.setTextViewText(R.id.update_time, "* updated just now");
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        Log.e("updated widget?", "yes");
                    } else {
                        remoteViews.setTextViewText(R.id.update_time, "* updated " + converted_time_passed_to_string + " ago");
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                        Log.e("updated widget?", "yes");
                    }
                }
            } catch (NullPointerException npe){
                npe.printStackTrace();
            }
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
                if(EUR_rate != null && pref.getString("preferred_currency", "USD").equalsIgnoreCase("EUR")) return Double.parseDouble(EUR_rate);
            } catch (Exception ex){
                ex.printStackTrace();
                //ignore
            }
            return 0.00;
        }

        private String convertTimePassedToString(Long timepassedinmilliseconds){
            if (TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds) <= 60l){
                if (TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds) == 1){
                    return String.format("%d second",
                            TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds)
                    );
                } else {
                    return String.format("%d secs",
                            TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds)
                    );
                }
            } else if (TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds) <= 60l){
                if(TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds) == 1){
                    return String.format("%d min",
                            TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds)
                    );
                } else {
                    return String.format("%d mins",
                            TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds)
                    );
                }
            } else if (TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds) <= 24l){
                if(TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds) == 1){
                    return String.format("%d hour",
                            TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds)
                    );
                } else {
                    return String.format("%d hours",
                            TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds)
                    );
                }
            } else {
                if(TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds) == 1){
                    return String.format("%d day",
                            TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds)
                    );
                } else {
                    return String.format("%d days",
                            TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds)
                    );
                }
            }
        }
    }
}