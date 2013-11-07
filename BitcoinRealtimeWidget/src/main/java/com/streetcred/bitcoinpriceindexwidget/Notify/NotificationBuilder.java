package com.streetcred.bitcoinpriceindexwidget.Notify;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Build;

public class NotificationBuilder {
    @SuppressLint("NewApi")
    public static Notification build(Notification.Builder builder) {
        if (Build.VERSION.SDK_INT < 16) {
            return builder.getNotification();
        }
        return builder.build();
    }
}
