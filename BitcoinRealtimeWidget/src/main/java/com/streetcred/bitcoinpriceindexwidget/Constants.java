package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by denniskong on 10/5/13.
 */
public class Constants {

    public static final String API_URL = "http://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String PREF_LAST_UPDATED_TIMESTAMP = "lastupdated_unixtime";
    public static final String PREF_LAST_UPDATED_PRICE = "lastupdated_price";
    public static final String PREF_LAST_UPDATED_CURRENCY = "preferred_currency";
    public static final String PREF_FREQ_SUMMARY = "Currently updating every ";

    public static final String FLAG_CLEAR_STACK = "clear_stack";
}
