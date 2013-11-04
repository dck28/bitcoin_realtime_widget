package com.streetcred.bitcoinpriceindexwidget;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/5/13.
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
        } else if (currency.equalsIgnoreCase("GBP")){
            return "英鎊";
        } else if (currency.equalsIgnoreCase("EUR")){
            return "歐元";
        }
        return "美元";
    }

    public static String convertCurrencyStringToChineseSimplified(String currency){
        if (currency.equalsIgnoreCase("USD")){
            return "美元";
        } else if (currency.equalsIgnoreCase("GBP")){
            return "英镑";
        } else if (currency.equalsIgnoreCase("EUR")){
            return "欧元";
        }
        return "美元";
    }
}
