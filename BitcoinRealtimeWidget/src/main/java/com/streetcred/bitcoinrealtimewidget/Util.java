package com.streetcred.bitcoinrealtimewidget;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/5/13.
 */
public class Util {
    public static long convertStringIntervalToLong(String interval_in_string_format){
        if ("1 minute".equalsIgnoreCase(interval_in_string_format)){
            return 60000;
        } else if ("2 minutes".equalsIgnoreCase(interval_in_string_format)){
            return 120000;
        } else if ("4 minutes".equalsIgnoreCase(interval_in_string_format)){
            return 240000;
        } else if ("8 minutes".equalsIgnoreCase(interval_in_string_format)){
            return 480000;
        } else if ("16 minutes".equalsIgnoreCase(interval_in_string_format)){
            return 960000;
        } else if ("32 minutes".equalsIgnoreCase(interval_in_string_format)){
            return 1920000;
        } else if ("1 hour".equalsIgnoreCase(interval_in_string_format)){
            return 3600000;
        } else if ("2 hours".equalsIgnoreCase(interval_in_string_format)){
            return 7200000;
        } else if ("4 hours".equalsIgnoreCase(interval_in_string_format)){
            return 14400000;
        } else if ("8 hours".equalsIgnoreCase(interval_in_string_format)){
            return 28800000;
        } else if ("16 hours".equalsIgnoreCase(interval_in_string_format)){
            return 57600000;
        } else if ("1 day".equalsIgnoreCase(interval_in_string_format)){
            return 86400000;
        } else {
            return 120000; //return default 2 minutes
        }
    }

    public static String getCurrentDisplayTime(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR); if (hours == 0) hours = 12;
        int minutes = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);
        String amORpm = (am_pm == 0) ? "AM" : "PM";
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + " " + amORpm;
    }

    public static String convertTimePassedToString(Long timepassedinmilliseconds){
        if (TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds) <= 60l){
            if (TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds) == 1){
                return String.format("%d second",
                        TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds)
                );
            } else {
                return String.format("%d secs",
                        TimeUnit.MILLISECONDS.toSeconds(timepassedinmilliseconds)
                );
            }
        } else if (TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds) <= 60l){
            if(TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds) == 1){
                return String.format("%d min",
                        TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds)
                );
            } else {
                return String.format("%d mins",
                        TimeUnit.MILLISECONDS.toMinutes(timepassedinmilliseconds)
                );
            }
        } else if (TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds) <= 24l){
            if(TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds) == 1){
                return String.format("%d hour",
                        TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds)
                );
            } else {
                return String.format("%d hours",
                        TimeUnit.MILLISECONDS.toHours(timepassedinmilliseconds)
                );
            }
        } else {
            if(TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds) == 1){
                return String.format("%d day",
                        TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds)
                );
            } else {
                return String.format("%d days",
                        TimeUnit.MILLISECONDS.toDays(timepassedinmilliseconds)
                );
            }
        }
    }
}
