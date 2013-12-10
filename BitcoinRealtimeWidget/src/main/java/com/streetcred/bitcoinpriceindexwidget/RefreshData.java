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
import com.streetcred.bitcoinpriceindexwidget.Notify.PriceOngoingNotification;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by StreetCred Inc. on 10/6/13.
 */

public class RefreshData extends AsyncTask<String, Void, String> {

    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;
    double newPrice;
    SharedPreferences pref;
    Context context;
    boolean isUpdateSuccessful;

    @Override
    protected void onPreExecute() {
        this.isUpdateSuccessful = true;
        this.context = XBTWidgetApplication.instance;
        this.appWidgetManager = AppWidgetManager.getInstance(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        thisWidget = new ComponentName(context, XBTRealtimeWidgetProvider.class);
        pref = XBTWidgetApplication.getSharedPreferences();
        remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"));
        remoteViews.setTextColor(R.id.price, Color.parseColor("#FFFFB2"));
        if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
            remoteViews.setTextViewText(R.id.update_time, "* 連接中...");
            pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 連接中...").commit();
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
            pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
            if(Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民幣")
                    || Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台幣")){
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
            } else {
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            }
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                    + " 提供報價");
            pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "由 " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                    + " 提供報價").commit();
        } else if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
            remoteViews.setTextViewText(R.id.update_time, "* 连接中...");
            pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 连接中...").commit();
            remoteViews.setTextViewText(R.id.exchange_currency,
                    Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
            pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
            if(Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民币")
                    || Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台币")){
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
            } else {
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            }
            remoteViews.setTextViewText(R.id.credit, "由 " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                    + " 提供报价");
            pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "由 " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                    + " 提供报价").commit();
        } else {
            remoteViews.setTextViewText(R.id.update_time, "* loading...");
            pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* loading...").commit();
            remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
            remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).commit();
            remoteViews.setTextViewText(R.id.credit, "Source: " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
            pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "Source: " +
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")).commit();
        }
        remoteViews = Util.saveRemoteViewsState(pref, remoteViews, context);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    protected String doInBackground(String... params) {
        String currency_to_retrieve = pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD");
        boolean shouldConvertToAlternateCurrency = false;
        boolean dataFromBTCChinaInCNY = false;
        if (!currency_to_retrieve.equalsIgnoreCase("USD")
                && !currency_to_retrieve.equalsIgnoreCase("GBP")
                && !currency_to_retrieve.equalsIgnoreCase("EUR")){
            shouldConvertToAlternateCurrency = true;
            currency_to_retrieve = "USD";
        }
        if (pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk").equalsIgnoreCase("BTC China")){
            dataFromBTCChinaInCNY = true;
        }

        try {
            String data_source = pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk");

            String data_source_url = getURL_from_source(data_source);

            Collection<BasicNameValuePair> requestParams = null;
            if (data_source_url.equals(Constants.COINBASE_API_URL)){
                requestParams = new ArrayList<BasicNameValuePair>();
                requestParams.add(new BasicNameValuePair("currency", currency_to_retrieve));
            } else if (data_source_url.equals(Constants.MTGOX_API_BASEURL)){
                data_source_url = data_source_url + "/BTC" + currency_to_retrieve + "/money/ticker_fast";
            }

            JSONObject json_response = RpcManager.getInstance().callGet(context, data_source_url, "", requestParams);
            newPrice = getNewPrice(json_response, data_source);

            if (dataFromBTCChinaInCNY && !pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("CNY")){
                JSONObject json_rate_response = RpcManager.getInstance().callGet(context, Constants.FOREX_RATE_API_URL, "");
                double rate_cny_to_usd = JSONParser.handle_getting_forex_exchange_for_btcchina_cny_to_usd(json_rate_response, pref);
                Log.e("Rate CNY to USD", Double.toString(rate_cny_to_usd));
                newPrice = Util.convertToUSDFromCNY(newPrice, rate_cny_to_usd);
                if (!pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("USD")){
                    double rate = JSONParser.handle_getting_forex_exchange_rate(json_rate_response, pref);
                    newPrice = Util.convertToSelectedAlternativeCurrencyFromUSD(newPrice, rate);
                    Log.e("forex rate", Double.toString(rate));
                }
            }

            if (shouldConvertToAlternateCurrency
                    && !dataFromBTCChinaInCNY){ // Get forex exchange and do conversion from Coinbase
                JSONObject json_rate_response = RpcManager.getInstance().callGet(context, Constants.FOREX_RATE_API_URL, "");
                double rate = JSONParser.handle_getting_forex_exchange_rate(json_rate_response, pref);
                newPrice = Util.convertToSelectedAlternativeCurrencyFromUSD(newPrice, rate);
                Log.e("forex rate", Double.toString(rate));
            }

            Log.e("newPrice", Double.toString(newPrice));

            if (newPrice != 0){
                DecimalFormat df = new DecimalFormat("0.0");
                String new_price_in_string = (df.format(newPrice));
                if( (df.format(newPrice)).length() >=6 ){
                    new_price_in_string = new_price_in_string.substring(0, new_price_in_string.indexOf('.'));
                    int indexOfCommaToBePlaced = new_price_in_string.length() - 3;
                    new_price_in_string = new_price_in_string.substring(0, indexOfCommaToBePlaced)
                            + "," + new_price_in_string.substring(indexOfCommaToBePlaced, new_price_in_string.length());
                }
                remoteViews.setTextViewText(R.id.price, new_price_in_string);
                String time_now_in_string = Util.getCurrentDisplayTime();
                if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 更新時間 " + time_now_in_string);
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 更新時間 " + time_now_in_string).commit();
                    remoteViews.setTextColor(R.id.price, Color.WHITE);
                } else if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 更新时间 " + time_now_in_string);
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 更新时间 " + time_now_in_string).commit();
                    remoteViews.setTextColor(R.id.price, Color.WHITE);
                } else {
                    remoteViews.setTextViewText(R.id.update_time, "* updated at " + time_now_in_string);
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* updated at " + time_now_in_string).commit();
                    remoteViews.setTextColor(R.id.price, Color.WHITE);
                }
                pref.edit()
                        .putLong(Constants.PREF_LAST_UPDATED_TIMESTAMP, System.currentTimeMillis())
                        .putString(Constants.PREF_LAST_UPDATED_PRICE, new_price_in_string)
                        .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, true)
                        .commit();
            } else {
                // Update widget info when no connection
                remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"));
                if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 綱絡未能連接").commit();
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    remoteViews.setTextViewText(R.id.exchange_currency,
                            Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
                    pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
                    if(Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民幣")
                            || Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台幣")){
                        remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
                    } else {
                        remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                    }
                    remoteViews.setTextViewText(R.id.credit, "由 "
                            + pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                            + " 提供報價");
                    pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "由 "
                            + pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                            + " 提供報價").commit();
                    pref.edit()
                            .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                            .commit();
                } else if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                    remoteViews.setTextViewText(R.id.update_time, "* 纲络未能连接");
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 纲络未能连接").commit();
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    remoteViews.setTextViewText(R.id.exchange_currency,
                            Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
                    pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
                    if(Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民币")
                            || Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台币")){
                        remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
                    } else {
                        remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                    }
                    remoteViews.setTextViewText(R.id.credit, "由 "
                            + pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                            + " 提供报价");
                    pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "由 "
                            + pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")
                            + " 提供报价").commit();
                    pref.edit()
                            .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                            .commit();
                } else {
                    remoteViews.setTextViewText(R.id.update_time, "* no connection");
                    pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* no connection").commit();
                    remoteViews.setTextColor(R.id.price, Color.GRAY);
                    remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                    remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
                    pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).commit();
                    remoteViews.setTextViewText(R.id.credit, "Source: " +
                            pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
                    pref.edit().putString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, "Source: " +
                            pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk")).commit();
                    pref.edit()
                            .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                            .commit();
                }
            }
            remoteViews = Util.saveRemoteViewsState(pref, remoteViews, context);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            pref.edit().putBoolean(Constants.WAS_LAST_UPDATE_SUCCESSFUL, true).commit();
            Log.e("Update Successful", "FLAGGED");
        } catch (Exception e) {
            e.printStackTrace(); Log.e("Exception at RefreshData", "Unsuccessful");
            Log.e("Exception at RefreshData", e.getMessage());
            pref.edit().putBoolean(Constants.WAS_LAST_UPDATE_SUCCESSFUL, false).commit();
            Log.e("Update Unsuccessful", "FLAGGED");
            isUpdateSuccessful = false;
            e.printStackTrace();
            remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"));
            if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 綱絡未能連接").commit();
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                remoteViews.setTextViewText(R.id.exchange_currency,
                        Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
                pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
                if(Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民幣")
                        || Util.convertCurrencyStringToChinese(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台幣")){
                    remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
                } else {
                    remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                }
                pref.edit()
                        .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                        .commit();
            } else if(pref.getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                remoteViews.setTextViewText(R.id.update_time, "* 纲络未能连接");
                pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 纲络未能连接").commit();
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                remoteViews.setTextViewText(R.id.exchange_currency,
                        Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));
                pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"))).commit();
                if(Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("人民币")
                        || Util.convertCurrencyStringToChineseSimplified(pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).equalsIgnoreCase("新台币")){
                    remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 14);
                } else {
                    remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                }
                pref.edit()
                        .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                        .commit();
            } else {
                remoteViews.setTextViewText(R.id.update_time, "* no connection");
                pref.edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* no connection").commit();
                remoteViews.setTextColor(R.id.price, Color.GRAY);
                remoteViews.setFloat(R.id.exchange_currency, "setTextSize", 20);
                remoteViews.setTextViewText(R.id.exchange_currency, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
                pref.edit().putString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")).commit();
                pref.edit()
                        .putBoolean(Constants.RECEIVED_VALID_NEW_PRICE, false)
                        .commit();
            }
            remoteViews = Util.saveRemoteViewsState(pref, remoteViews, context);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

        if (persistentNotificationEnabled()){
            if(isUpdateSuccessful){
                PriceOngoingNotification.hit(context,
                    pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"),
                    pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"),
                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"),
                    pref.getBoolean(Constants.PREF_IS_FROM_ONGOING_NOTIFICATION, false));
            } else {
                PriceOngoingNotification.noConnection(context);
            }
        }
        pref.edit().putBoolean(Constants.PREF_IS_FROM_ONGOING_NOTIFICATION, false).commit();

        // Check if alert exist (set by user)
        // if newPrice is higher than limit alert - hit
//        if(AlertExist()){
//            double limit = getPriceAlertLimit();
//            double stop = getPriceAlertStop();
//            if (newPrice > limit || newPrice < stop){
//                PriceOngoingNotification.hit(context,
//                        Double.toString(newPrice),
//                        pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"),
//                        pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));
//            }
//        }
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

        if (from.equals("BTC China")){
            return JSONParser.handle_source_BTCChina(json_response);
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
        } else if(data_source.equalsIgnoreCase("BTC China")){
            return Constants.BTCChina;
        }
        //Default
        return Constants.COINDESK_API_URL;
    }

    private boolean persistentNotificationEnabled(){
        return pref.getBoolean(Constants.PREF_ONGOING_NOTIFICATION, true);
    }

}
