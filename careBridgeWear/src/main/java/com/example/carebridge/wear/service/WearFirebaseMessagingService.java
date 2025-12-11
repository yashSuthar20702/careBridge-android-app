package com.example.carebridge.wear.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.carebridge.wear.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class WearFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "carebridge_wear_channel";
    private static final String TAG = "WearFCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Wear Data Payload: " + remoteMessage.getData());
            handleData(remoteMessage.getData());
        }
    }

    private void handleData(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("body");
        String logId = data.get("log_id");

        if (logId == null) return;

        if (title == null) title = "💊 Medicine Reminder";
        if (message == null) message = "Tap to confirm";

        sendWearNotification(title, message, logId);
    }

    private void sendWearNotification(String title, String message, String logId) {

        int id = Integer.parseInt(logId);

        // Intent for "Taken"
        Intent takenIntent = new Intent(this, WearNotificationReceiver.class);
        takenIntent.setAction("WEAR_MED_TAKEN");
        takenIntent.putExtra("log_id", logId);
        takenIntent.putExtra("taken_status", "Taken");

        PendingIntent takenPending = PendingIntent.getBroadcast(
                this,
                id + 1,
                takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent for "Not Taken"
        Intent notTakenIntent = new Intent(this, WearNotificationReceiver.class);
        notTakenIntent.setAction("WEAR_MED_NOT_TAKEN");
        notTakenIntent.putExtra("log_id", logId);
        notTakenIntent.putExtra("taken_status", "Not Taken");

        PendingIntent notTakenPending = PendingIntent.getBroadcast(
                this,
                id + 2,
                notTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager manager = getSystemService(NotificationManager.class);

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Wear Medication Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // Build and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_health)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[]{200, 200})
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_check, "Taken", takenPending)
                .addAction(R.drawable.ic_close, "Not Taken", notTakenPending);

        manager.notify(id, builder.build());
    }
}
