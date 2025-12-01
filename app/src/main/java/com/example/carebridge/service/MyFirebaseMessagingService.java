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

    // ✅ store logId from FCM data
    private String lastLogId = null;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "✅ New FCM Token: " + token);
        // send token to your backend
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "📩 Message received: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "📦 Data Payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        try {
            String title = data.get("title");
            String message = data.get("body");

            // ✅ EXTRACT log_id HERE
            lastLogId = data.get("log_id");

            Log.d(TAG, "📌 Extracted log_id: " + lastLogId);

            if (title == null) title = "CareBridge";
            if (message == null) message = "You have a new update.";

            sendNotification(title, message);

        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error handling data message: " + e.getMessage(), e);
        }
    }

    private void sendNotification(String title, String messageBody) {

        // ❗ logId is now stored earlier
        String logId = lastLogId;

        if (logId == null) {
            Log.e(TAG, "❌ log_id missing in FCM payload.");
            return; // stop to avoid null crash
        }

        // 👉 ACTION: Taken
        Intent takenIntent = new Intent(this, NotificationActionReceiver.class);
        takenIntent.setAction("MED_TAKEN");
        takenIntent.putExtra("log_id", logId);

        PendingIntent takenPending = PendingIntent.getBroadcast(
                this, 1, takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 👉 ACTION: Not Taken
        Intent notTakenIntent = new Intent(this, NotificationActionReceiver.class);
        notTakenIntent.setAction("MED_NOT_TAKEN");
        notTakenIntent.putExtra("log_id", logId);

        PendingIntent notTakenPending = PendingIntent.getBroadcast(
                this, 2, notTakenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "CareBridge Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
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
                .addAction(R.drawable.ic_health, "Not Taken", notTakenPending);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
