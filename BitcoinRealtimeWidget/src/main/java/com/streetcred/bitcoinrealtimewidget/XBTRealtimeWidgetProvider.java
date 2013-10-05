package com.streetcred.bitcoinrealtimewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
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
        timer.scheduleAtFixedRate(new TimedAPICall(context, appWidgetManager), 1, 60000);
    }

    private class TimedAPICall extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        double newPrice;
        SharedPreferences pref;

        public TimedAPICall(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            thisWidget = new ComponentName(context, XBTRealtimeWidgetProvider.class);
            pref = XBTWidgetApplication.getSharedPreferences();
        }

        @Override
        public void run() {
            try{
                Long updatedSince = System.currentTimeMillis() - pref.getLong(Constants.LAST_UPDATED_TIMESTAMP, System.currentTimeMillis());
                String converted_time_passed_to_string = convertTimePassedToString(updatedSince);
                newPrice = getNewPrice();
                DecimalFormat df = new DecimalFormat("0.0");
                remoteViews.setTextViewText(R.id.price, df.format(newPrice));
                remoteViews.setTextViewText(R.id.update_time, "* updated " + converted_time_passed_to_string + " ago");
                pref.edit().putLong(Constants.LAST_UPDATED_TIMESTAMP, System.currentTimeMillis()).commit();
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            } catch (NullPointerException npe){
                //ignore
            }
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