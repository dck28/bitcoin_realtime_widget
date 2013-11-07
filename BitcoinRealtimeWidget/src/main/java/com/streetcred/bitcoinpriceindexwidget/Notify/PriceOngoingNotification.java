package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.streetcred.bitcoinpriceindexwidget.Constants;
import com.streetcred.bitcoinpriceindexwidget.MainActivity;
import com.streetcred.bitcoinpriceindexwidget.R;

/**
 * Created by denniskong on 11/7/13.
 */
public class PriceOngoingNotification {

    public static void hit(Context context, String price, String currencyDenomination, String dataSource){
        NotificationManager nm = getNotificationManager(context);
        // Point intent to desired package name and activity
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra(Constants.FLAG_CLEAR_STACK, true);
        // Setup notification pending intent
        PendingIntent launchMainIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final String title = price + " " + currencyDenomination;
        final String text = "Data from " + dataSource + ".";
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false) //Disable click to cancel
                .setContentIntent(launchMainIntent); // Launch MainActivity as a result of the click
                if (Build.VERSION.SDK_INT >= 16) {
                    builder.setPriority(Notification.PRIORITY_HIGH);
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
