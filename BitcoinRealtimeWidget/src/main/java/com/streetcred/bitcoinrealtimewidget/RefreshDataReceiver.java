package com.streetcred.bitcoinrealtimewidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by denniskong on 10/6/13.
 */
public class RefreshDataReceiver extends BroadcastReceiver{
    public static final String ACTION = "com.streetcred.bitcoinrealtimewidget.REFRESH_REQUEST";
    @Override
    public void onReceive(Context context, Intent intent) {
        RefreshData refresh = new RefreshData();
        refresh.execute();
    }
}