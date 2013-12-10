package com.streetcred.bitcoinpriceindexwidget.Exchange;

/**
 * Created by StreetCred Inc. on 11/4/13.
 */
public class Exchange{

    private String APIBaseUrl;
    private String[] supported_currencies;

    public Exchange(){}

    public Exchange(String APIBaseUrl, String[] supported_currencies){
        this.APIBaseUrl = APIBaseUrl;
        this.supported_currencies = supported_currencies;
    }

    public String getAPIBaseUrl(){
        return APIBaseUrl;
    }

    public String[] getSupportedCurrencies() {
        return supported_currencies;
    }
}
