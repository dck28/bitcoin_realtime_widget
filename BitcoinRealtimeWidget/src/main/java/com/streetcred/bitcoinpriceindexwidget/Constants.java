package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by StreetCred Inc. on 10/5/13.
 */
public class Constants {

    public static final String PREF_DONATE_EVER_CLICKED = "ever_clicked_donate";
    public static final String PREF_RATE_EVER_CLICKED = "ever_clicked_rate";

    public static final String FOREX_RATE_API_URL = "https://coinbase.com/api/v1/currencies/exchange_rates";

    public static final String COINDESK_API_URL = "http://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String COINBASE_API_URL = "https://coinbase.com/api/v1/prices/spot_rate";
    public static final String MTGOX_API_BASEURL = "https://data.mtgox.com/api/2";
    public static final String BTCChina = "https://data.btcchina.com/data/ticker";
    public static final String BITSTAMP_API_URL = "https://www.bitstamp.net/api/ticker/";
    public static final String BTCE_API_URL = "https://btc-e.com/api/3/ticker/btc_usd";
    public static final String BITFINEX_API_URL = "https://api.bitfinex.com/v1/ticker/btcusd";
    public static final String KRAKEN_API_URL = "https://api.kraken.com/0/public/Ticker?pair=XXBTZUSD";
    public static final String ANXBTC_API_URL = "https://anxpro.com/api/2/BTCUSD/money/ticker";
    public static final String ITBIT_API_URL = "https://api.itbit.com/v1/markets/XBTUSD/ticker";

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

    //    public static final String SAVE_PRICE_WIDGET_STATE = "save_price_widget_state";
    //    Using PREF_LAST_UPDATED_PRICE instead of a new key
    public static final String SAVE_BACKGROUND_WIDGET_STATE = "save_background_widget_state";
    public static final String SAVE_EXCHANGE_CURRENCY_WIDGET_STATE = "save_exchange_currency_widget_state";
    public static final String SAVE_UPDATE_TIME_WIDGET_STATE = "save_update_time_widget_state";
    public static final String SAVE_DISPLAY_CREDIT_WIDGET_STATE = "save_display_credit_widget_state";

}
