package com.streetcred.bitcoinpriceindexwidget.Refresh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.XBTWidgetApplication;

/**
 * Created by denniskong on 11/8/13.
 */

public class RefreshIntervalManager {
    private static final RefreshIntervalManager instance = new RefreshIntervalManager();

    private RefreshIntervalManager() {
    }

    public static RefreshIntervalManager instance() {
        return instance;
    }

    public void startIntervalRefreshing(final Context context, boolean startImmediately) {
        final AlarmManager alarmManager = getAlarmService(context);

        PendingIntent pendingSyncIntent = createPendingIntent(context);

        String interval_as_string = XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_REFRESH_INTERVAL, "-1");
        if(!"-1".equalsIgnoreCase(interval_as_string)){
            long interval = Long.parseLong(interval_as_string);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (startImmediately ? 0 : interval),
                    interval, pendingSyncIntent);
        }
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context.getApplicationContext(), RefreshIntervalReceiver.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private AlarmManager getAlarmService(Context context) {
        return (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void cancelIntervalRefreshing(final Context context) {
        getAlarmService(context).cancel(createPendingIntent(context));
    }
}
