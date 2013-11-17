package com.streetcred.bitcoinpriceindexwidget.ConnectionManager;

import android.content.SharedPreferences;
import android.util.Log;

import com.streetcred.bitcoinpriceindexwidget.Constants;

import org.json.JSONObject;

/**
 * Created by denniskong on 10/30/13.
 */
public class JSONParser {

    public static double handle_source_COINDESK(JSONObject json_response, SharedPreferences pref){
        String USD_code = null; String USD_symbol = null; String USD_rate = null;
        String USD_description = null; String USD_rate_float = null;
        String GBP_code = null; String GBP_symbol = null; String GBP_rate = null;
        String GBP_description = null; String GBP_rate_float = null;
        String EUR_code = null; String EUR_symbol = null; String EUR_rate = null;
        String EUR_description = null; String EUR_rate_float = null;

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
        if(USD_rate != null && pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("USD")
                || (!pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("USD")
                && !pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("GBP")
                && !pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("EUR")))
            return Double.parseDouble(USD_rate);

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
        if(GBP_rate != null && pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD").equalsIgnoreCase("GBP")) return Double.parseDouble(GBP_rate);

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

        //Default
        return 0.00;
    }

    public static double handle_source_COINBASE(JSONObject json_response){

        String rate = json_response.optString("amount");
        if(rate != null) return Double.parseDouble(rate);

        //Default
        return 0.00;
    }

    public static double handle_source_MTGOX(JSONObject json_response){

        JSONObject data = json_response.optJSONObject("data");
        if(data != null){
            JSONObject last = data.optJSONObject("last");
            if (last != null){
                String value = last.optString("value");
                return Double.parseDouble(value);
            }
        }

        //Default
        return 0.00;
    }

    public static double handle_getting_forex_exchange_rate(JSONObject json_response, SharedPreferences pref){
        return Double.parseDouble(json_response
                        .optString("usd_to_" + pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")
                                .toLowerCase()));
    }

}