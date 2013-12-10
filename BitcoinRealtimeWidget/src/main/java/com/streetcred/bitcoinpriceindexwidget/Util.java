package com.streetcred.bitcoinpriceindexwidget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by StreetCred Inc. on 10/5/13.
 */
public class Util {

    public static String getCurrentDisplayTime(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR); if (hours == 0) hours = 12;
        int minutes = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);
        String amORpm = (am_pm == 0) ? "AM" : "PM";
        if(XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")
                || XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
            if (amORpm.equalsIgnoreCase("AM")){
                amORpm = "上午";
            } else if (amORpm.equalsIgnoreCase("PM")){
                amORpm = "下午";
            }
        }
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + " " + amORpm;
    }

    public static String convertCurrencyStringToChinese(String currency){
        if (currency.equalsIgnoreCase("USD")){
            return "美元";
        } else if (currency.equalsIgnoreCase("CAD")){
            return "加幣";
        } else if (currency.equalsIgnoreCase("GBP")){
            return "英鎊";
        } else if (currency.equalsIgnoreCase("EUR")){
            return "歐元";
        } else if (currency.equalsIgnoreCase("CNY")){
            return "人民幣";
        } else if (currency.equalsIgnoreCase("HKD")){
            return "港元";
        } else if (currency.equalsIgnoreCase("TWD")){
            return "新台幣";
        } else if (currency.equalsIgnoreCase("AUD")){
            return "澳元";
        }
        return "美元";
    }

    public static String convertCurrencyStringToChineseSimplified(String currency){
        if (currency.equalsIgnoreCase("USD")){
            return "美元";
        } else if (currency.equalsIgnoreCase("CAD")){
            return "加币";
        } else if (currency.equalsIgnoreCase("GBP")){
            return "英镑";
        } else if (currency.equalsIgnoreCase("EUR")){
            return "欧元";
        } else if (currency.equalsIgnoreCase("CNY")){
            return "人民币";
        } else if (currency.equalsIgnoreCase("HKD")){
            return "港元";
        } else if (currency.equalsIgnoreCase("TWD")){
            return "新台币";
        } else if (currency.equalsIgnoreCase("AUD")){
            return "澳元";
        }
        return "美元";
    }



    public static String convertCurrencyChineseToEnglish(String newValue){
        if(newValue.equals("美元")){
            return "USD";
        } else if(newValue.equals("加幣")){
            return "CAD";
        } else if(newValue.equals("英鎊")){
            return "GBP";
        } else if(newValue.equals("歐元")){
            return "EUR";
        } else if(newValue.equals("人民幣")){
            return "CNY";
        } else if(newValue.equals("港元")){
            return "HKD";
        } else if(newValue.equals("新台幣")){
            return "TWD";
        } else if(newValue.equals("澳元")){
            return "AUD";
        }
        return "USD";
    }

    public static String convertCurrencyEnglishToChinese(String newValue){
        if(newValue.equals("USD")){
            return "美元";
        } else if(newValue.equals("CAD")){
            return "加幣";
        } else if(newValue.equals("GBP")){
            return "英鎊";
        } else if(newValue.equals("EUR")){
            return "歐元";
        } else if(newValue.equals("CNY")){
            return "人民幣";
        } else if(newValue.equals("HKD")){
            return "港元";
        } else if(newValue.equals("TWD")){
            return "新台幣";
        } else if(newValue.equals("AUD")){
            return "澳元";
        }
        return "美元";
    }

    public static String convertCurrencyChineseSimplifiedToEnglish(String newValue){
        if(newValue.equals("美元")){
            return "USD";
        } else if(newValue.equals("加币")){
            return "CAD";
        } else if(newValue.equals("英镑")){
            return "GBP";
        } else if(newValue.equals("欧元")){
            return "EUR";
        } else if(newValue.equals("人民币")){
            return "CNY";
        } else if(newValue.equals("港元")){
            return "HKD";
        } else if(newValue.equals("新台币")){
            return "TWD";
        } else if(newValue.equals("澳元")){
            return "AUD";
        }
        return "USD";
    }

    public static String convertCurrencyEnglishToChineseSimplified(String newValue){
        if(newValue.equals("USD")){
            return "美元";
        } else if(newValue.equals("CAD")){
            return "加币";
        } else if(newValue.equals("GBP")){
            return "英镑";
        } else if(newValue.equals("EUR")){
            return "欧元";
        } else if(newValue.equals("CNY")){
            return "人民币";
        } else if(newValue.equals("HKD")){
            return "港元";
        } else if(newValue.equals("TWD")){
            return "新台币";
        } else if(newValue.equals("AUD")){
            return "澳元";
        }
        return "美元";
    }

    public static String convertThemeChineseToEnglish(String newValueChinese){
        if(newValueChinese.equals("深藍")){
            return "Navy";
        } else if(newValueChinese.equals("金橘")){
            return "Clementine";
        } else if(newValueChinese.equals("漂浮")){
            return "Float";
        } else if(newValueChinese.equals("磨砂玻璃")){
            return "Frost";
        }
        return "Float";
    }

    public static String convertThemeEnglishToChinese(String newValueEnglish){
        if(newValueEnglish.equals("Navy")){
            return "深藍";
        } else if(newValueEnglish.equals("Clementine")){
            return "金橘";
        } else if(newValueEnglish.equals("Float")){
            return "漂浮";
        } else if(newValueEnglish.equals("Frost")){
            return "磨砂玻璃";
        }
        return "漂浮";
    }


    public static String convertThemeChineseSimplifiedToEnglish(String newValueChinese){
        if(newValueChinese.equals("深蓝")){
            return "Navy";
        } else if(newValueChinese.equals("金橘")){
            return "Clementine";
        } else if(newValueChinese.equals("漂浮")){
            return "Float";
        } else if(newValueChinese.equals("磨砂玻璃")){
            return "Frost";
        }
        return "Float";
    }

    public static String convertThemeEnglishToChineseSimplified(String newValueEnglish){
        if(newValueEnglish.equals("Navy")){
            return "深蓝";
        } else if(newValueEnglish.equals("Clementine")){
            return "金橘";
        } else if(newValueEnglish.equals("Float")){
            return "漂浮";
        } else if(newValueEnglish.equals("Frost")){
            return "磨砂玻璃";
        }
        return "漂浮";
    }

    public static double convertToSelectedAlternativeCurrencyFromUSD(double price, double rate){
        return price*rate;
    }

    public static double convertToUSDFromCNY(double price, double rate){
        return price*rate;
    }

    public static RemoteViews saveRemoteViewsState(SharedPreferences pref, RemoteViews remoteViews, Context context){

        // Set background
        remoteViews.setInt(R.id.background, "setBackgroundColor"
                , pref.getInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.TRANSPARENT));

        // Set price
        remoteViews.setTextViewText(R.id.price, pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"));

        // Set widget price textview refreshable/clickable
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.price, refreshPendingIntent);

        // Set exchange currency display
        remoteViews.setTextViewText(R.id.exchange_currency,
                pref.getString(Constants.SAVE_EXCHANGE_CURRENCY_WIDGET_STATE, "USD"));

        // Set app_icon clickable
        Intent configIntent = new Intent(context, MainActivity.class);
        configIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.app_icon, configPendingIntent);

        // Set updatetime string
        remoteViews.setTextViewText(R.id.update_time, pref.getString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, ""));

        // Set credit display string
        remoteViews.setTextViewText(R.id.credit, pref.getString(Constants.SAVE_DISPLAY_CREDIT_WIDGET_STATE, ""));

        return remoteViews;

    }
}
