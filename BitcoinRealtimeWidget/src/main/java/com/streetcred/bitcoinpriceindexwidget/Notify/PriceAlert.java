package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.MainActivity;
import com.streetcred.bitcoinpriceindexwidget.R;

/**
 * Created by denniskong on 11/7/13.
 */
public class PriceAlert {

    public static void hit(Context context, String price, String currencyDenomination, String dataSource){
        NotificationManager nm = getNotificationManager(context);
        // Point intent to desired package name and activity
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(Constants.FLAG_CLEAR_STACK, true);
        // Setup notification pending intent
        PendingIntent launchMainIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final String ticker = "Bitcoin Price Alert: " + price + " " + currencyDenomination + " at " + dataSource + ".";
        final String title = price + " " + currencyDenomination;
        final String text = "at " + dataSource + ".";
        Notification.Builder builder = new Notification.Builder(context)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true) //Dismiss notification when clicked
                .setContentIntent(launchMainIntent); // Launch MainActivity as a result of the click
        // Create system notification with light
        Notification notify = NotificationBuilder.build(builder);
        notify.flags |= Notification.FLAG_SHOW_LIGHTS;
        notify.ledARGB = 0xffffffff;
        notify.ledOnMS = 300;
        notify.ledOffMS = 1000;
        nm.notify(R.id.price_alert_notification_id, notify);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
