package com.streetcred.bitcoinpriceindexwidget;

import java.util.Calendar;

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
}
