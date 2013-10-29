package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by denniskong on 10/5/13.
 */
public class Constants {

    public static final String COINDESK_API_URL = "http://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String COINBASE_API_URL = "https://coinbase.com/api/v1/prices/spot_rate";

    public static final String PREF_LAST_UPDATED_TIMESTAMP = "lastupdated_unixtime";
    public static final String PREF_LAST_UPDATED_PRICE = "lastupdated_price";
    public static final String PREF_LAST_UPDATED_DATA_SOURCE = "preferred_data_source";
    public static final String PREF_LAST_UPDATED_CURRENCY = "preferred_currency";
    public static final String PREF_LAST_UPDATED_THEME = "preferred_theme";
    public static final String PREF_DISPLAY_LANGUAGE = "preferred_display_language";

    public static final String FLAG_CLEAR_STACK = "clear_stack";
}
