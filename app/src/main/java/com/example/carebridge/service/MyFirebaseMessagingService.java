package com.example.carebridge.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.carebridge.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private static final String CHANNEL_ID = "carebridge_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "📦 Data Payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        try {
            String title = data.get("title");
            String message = data.get("body");
            String logId = data.get("log_id");

            if (logId == null) {
                Log.e(TAG, "❌ Missing log_id in FCM data");
                return;
            }

            if (title == null) title = "Medicine Reminder 💊";
            if (message == null) message = "Please confirm your medication status";

            sendNotification(title, message, logId);

        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error handling FCM data: " + e.getMessage());
        }
    }

    private void sendNotification(String title, String messageBody, String logId) {

        int logIdInt;
        try {
            logIdInt = Integer.parseInt(logId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid log_id format: " + logId);
            return;
        }

        // --- PendingIntents ---
        Intent takenIntent = new Intent(this, NotificationActionReceiver.class);
        takenIntent.setAction("MED_TAKEN");
        takenIntent.putExtra("log_id", logId);
        takenIntent.putExtra("taken_status", "Taken");

        PendingIntent takenPending = PendingIntent.getBroadcast(
                this, logIdInt * 10 + 1, takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent notTakenIntent = new Intent(this, NotificationActionReceiver.class);
        notTakenIntent.setAction("MED_NOT_TAKEN");
        notTakenIntent.putExtra("log_id", logId);
        notTakenIntent.putExtra("taken_status", "Not Taken");

        PendingIntent notTakenPending = PendingIntent.getBroadcast(
                this, logIdInt * 10 + 2, notTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "CareBridge Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(soundUri, null);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_health)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_check, "Taken", takenPending)
                .addAction(R.drawable.ic_close, "Not Taken", notTakenPending);

        nm.notify(logIdInt, builder.build()); // Use logId as unique notification ID
    }
}
