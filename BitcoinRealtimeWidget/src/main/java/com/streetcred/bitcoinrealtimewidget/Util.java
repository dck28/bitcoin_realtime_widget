package com.streetcred.bitcoinrealtimewidget;

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
}
