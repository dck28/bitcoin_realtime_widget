package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.R;
import com.streetcred.bitcoinpriceindexwidget.RefreshDataReceiver;
import com.streetcred.bitcoinpriceindexwidget.Util;
import com.streetcred.bitcoinpriceindexwidget.XBTWidgetApplication;

/**
 * Created by denniskong on 11/7/13.
 */
public class PriceOngoingNotification {

    public static void hit(Context context, String price, String currencyDenomination, String dataSource, boolean showTicker){
        NotificationManager nm = getNotificationManager(context);
        // Setup notification pending intent
        Intent refreshIntent = new Intent(context, RefreshDataReceiver.class);
        refreshIntent.setAction(RefreshDataReceiver.ACTION_UPDATE_WITH_TICKER);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Prepare data source disclaimer text & title
        String text = "Data provided by " + dataSource + ".";

        //Translate (Default input Strings are always in English)
        if (XBTWidgetApplication.getSharedPreferences()
            .getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
            .equalsIgnoreCase("中文(繁體)")){

            currencyDenomination = Util.convertCurrencyStringToChinese(currencyDenomination);
            text = "由 " + dataSource + " 提供報價";

        } else if (XBTWidgetApplication.getSharedPreferences()
            .getString(Constants.PREF_DISPLAY_LANGUAGE, "English")
            .equalsIgnoreCase("中文(简体)")){

            currencyDenomination = Util.convertCurrencyStringToChineseSimplified(currencyDenomination);
            text = "由 " + dataSource + " 提供报价";

        }
        final String title = price + " " + currencyDenomination;

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false) //Disable click to cancel
                .setContentIntent(refreshPendingIntent); // Refresh and show ticker as a result of the click
                if (Build.VERSION.SDK_INT >= 16) {
                    builder.setPriority(Notification.PRIORITY_HIGH);
                }
                if (showTicker){
                    builder.setTicker(price + " " + currencyDenomination + ", " + dataSource);
                }
        // Create system notification
        Notification notify = NotificationBuilder.build(builder);
        notify.flags |= Notification.FLAG_ONGOING_EVENT;
        nm.notify(R.id.price_ongoing_notification_id, notify);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
