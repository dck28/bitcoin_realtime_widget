package com.streetcred.bitcoinpriceindexwidget.Refresh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.R;
import com.streetcred.bitcoinpriceindexwidget.XBTWidgetApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by StreetCred Inc. on 11/8/13.
 */

public class RefreshIntervalReceiver extends BroadcastReceiver {
    private static final String LOCK_NAME = "lock." + RefreshIntervalReceiver.class.getName();
    private static final String LOG_TAG = RefreshIntervalReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) Log.e("Screen Unlock","detected");
        Log.e("RefreshIntervalReceiver", "Received");
        Log.e("wasLastUpdateSuccessful?", Boolean.toString(wasLastUpdateSuccessful()));
        Log.e("wasTimeEllapsedOver5Seconds?", Boolean.toString(timeElapsedOver5SecondsSinceLastReceived()));

        if ( (Intent.ACTION_USER_PRESENT.equals(intent.getAction()) && wasLastUpdateSuccessful()) ){
            // ignore
        } else if (timeElapsedOver5SecondsSinceLastReceived() ||
                (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)){ //Added check for the issue of receiving twice

            XBTWidgetApplication.getSharedPreferences().edit()
                    .putLong(Constants.REFRESH_INTERVAL_LAST_RECEIVED, System.currentTimeMillis()).commit();

            Handler handler = new Handler();
            PowerManager.WakeLock lock = PowerLockProvider.acquireLock(context, LOCK_NAME, 10000l, handler);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            double previous_price = Double.parseDouble(
                    XBTWidgetApplication
                            .getSharedPreferences()
                            .getString(Constants.PREF_LAST_UPDATED_PRICE, "0.0")
                            .replace(",",""));
            // Fix for invalid parseDouble when Data from API is malformed: e.g. ""
            // Nov 8, 2013 : java.lang.NumberFormatException


            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(
                    new RefreshRequestLauncher(
                            lock,
                            context,
                            handler,
                            remoteViews,
                            previous_price));

            try{
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean wasLastUpdateSuccessful(){
        return XBTWidgetApplication.getSharedPreferences()
                .getBoolean(Constants.WAS_LAST_UPDATE_SUCCESSFUL, true);
    }

    private boolean timeElapsedOver5SecondsSinceLastReceived(){
        return (System.currentTimeMillis() - XBTWidgetApplication.getSharedPreferences()
                .getLong(Constants.REFRESH_INTERVAL_LAST_RECEIVED, 0) > 5000l );
    }

}
