package com.example.carebridge.service;

import android.app.NotificationManager;
import android.content.Context;

public class NotificationHelper {

    public static void clearNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(id);
        }
    }
}
