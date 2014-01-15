package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by StreetCred Inc. on 11/7/13.
 */
public class Alert implements Serializable, Parcelable{

    private String alertPriceInUSD;
    private String dataSource;

    public Alert(String alertPriceInUSD, String dataSource, String currencyDenomination){
        this.alertPriceInUSD = alertPriceInUSD;
        this.dataSource = dataSource;
    }

    // Getters & Setters
    public String getAlertPriceInUSD(){ return alertPriceInUSD; }
    public void setAlertPriceInUSD(String alertPriceInUSD){ this.alertPriceInUSD = alertPriceInUSD; }

    public String getDataSource(){ return dataSource; }
    public void setDataSource(String dataSource){ this.dataSource = dataSource; }


    // Parcelling part
    public Alert(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.alertPriceInUSD = data[0];
        this.dataSource = data[1];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.alertPriceInUSD,
                this.dataSource});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };
}
