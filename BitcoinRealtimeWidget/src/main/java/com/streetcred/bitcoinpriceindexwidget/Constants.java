package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by denniskong on 10/5/13.
 */
public class Constants {

    public static final String FOREX_RATE_API_URL = "https://rate-exchange.appspot.com/currency";

    public static final String COINDESK_API_URL = "http://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String COINBASE_API_URL = "https://coinbase.com/api/v1/prices/spot_rate";
    public static final String MTGOX_API_BASEURL = "https://data.mtgox.com/api/2";

    public static final String PREF_REFRESH_INTERVAL = "preference_refresh_interval";
    public static final String PREF_ONGOING_NOTIFICATION = "preference_ongoing_notification";
    public static final String PREF_LAST_UPDATED_TIMESTAMP = "lastupdated_unixtime";
    public static final String PREF_LAST_UPDATED_PRICE = "lastupdated_price";
    public static final String PREF_LAST_UPDATED_DATA_SOURCE = "preferred_data_source";
    public static final String PREF_LAST_UPDATED_CURRENCY = "preferred_currency";
    public static final String PREF_AUXILIARY_CURRENCY_CONVERSION = "auxiliary_currency_conversion_usd_base";
    public static final String PREF_LAST_UPDATED_THEME = "preferred_theme";
    public static final String PREF_DISPLAY_LANGUAGE = "preferred_display_language";
    public static final String PREF_IS_FROM_ONGOING_NOTIFICATION = "is_from_ongoing_notification";

    public static final String FLAG_CLEAR_STACK = "clear_stack";
    public static final String RECEIVED_VALID_NEW_PRICE = "new_price_is_valid";
    public static final String WAS_LAST_UPDATE_SUCCESSFUL = "was_last_update_successful";
    public static final String REFRESH_INTERVAL_LAST_RECEIVED = "last_refresh_interval_update_timestamp";
}
