package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by denniskong on 11/7/13.
 */
public class Limit implements Serializable, Parcelable{

    private String limit;
    private String dataSource;
    private String currencyDenomination;

    public Limit(String limit, String dataSource, String currencyDenomination){
        this.limit = limit;
        this.dataSource = dataSource;
        this.currencyDenomination = currencyDenomination;
    }

    // Getters & Setters
    public String getLimit(){ return limit; }
    public void setLimit(String limit){ this.limit = limit; }

    public String getDataSource(){ return dataSource; }
    public void setDataSource(String dataSource){ this.dataSource = dataSource; }

    public String getCurrencyDenomination(){ return currencyDenomination; }
    public void setCurrencyDenomination(String currencyDenomination){ this.currencyDenomination = currencyDenomination; }


    // Parcelling part
    public Limit(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.limit = data[0];
        this.dataSource = data[1];
        this.currencyDenomination = data[2];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.limit,
                this.dataSource,
                this.currencyDenomination});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Limit createFromParcel(Parcel in) {
            return new Limit(in);
        }

        public Limit[] newArray(int size) {
            return new Limit[size];
        }
    };
}
