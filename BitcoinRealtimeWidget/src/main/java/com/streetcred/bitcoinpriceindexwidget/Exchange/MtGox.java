package com.streetcred.bitcoinpriceindexwidget.Exchange;

import com.streetcred.bitcoinpriceindexwidget.Constants;

/**
 * Created by StreetCred Inc. on 11/4/13.
 */
public class MtGox extends Exchange{

    private static Exchange mtgox = new Exchange(Constants.MTGOX_API_BASEURL, new String [] {"USD", "GBP", "EUR"});

}
