package com.example.carebridge.wear.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * WearFirebaseMessagingService

 * Handles Firebase Cloud Messaging (FCM) notifications
 * for the Wear OS application.

 * Mainly used for medicine reminders.
 */
public class WearFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WearFCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "FCM data payload received");
            handleData(remoteMessage.getData());
        }
    }

    /**
     * Called when a new FCM token is generated.
     * Useful for syncing token with backend if needed.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token received");
    }

    /**
     * Extracts and validates notification data.
     */
    private void handleData(@NonNull Map<String, String> data) {

        String logId = data.get(Constants.KEY_LOG_ID);

        if (logId == null || logId.isEmpty()) {
            Log.e(TAG, "Invalid log ID received");
            return;
        }

        String title = data.get(Constants.KEY_TITLE);
        if (title == null || title.isEmpty()) {
            title = getString(R.string.notification_medicine_title);
        }

        String message = data.get(Constants.KEY_BODY);
        if (message == null || message.isEmpty()) {
            message = getString(R.string.notification_medicine_message);
        }

        sendWearNotification(title, message, logId);
    }

    /**
     * Builds and displays Wear OS notification.
     */
    private void sendWearNotification(
            @NonNull String title,
            @NonNull String message,
            @NonNull String logId
    ) {

        int notificationId;
        try {
            notificationId = Integer.parseInt(logId);
        } catch (NumberFormatException e) {
            notificationId = (int) System.currentTimeMillis();
        }

        PendingIntent takenPendingIntent =
                createActionPendingIntent(
                        Constants.ACTION_MED_TAKEN,
                        logId,
                        Constants.STATUS_TAKEN,
                        notificationId + 1
                );

        PendingIntent notTakenPendingIntent =
                createActionPendingIntent(
                        Constants.ACTION_MED_NOT_TAKEN,
                        logId,
                        Constants.STATUS_NOT_TAKEN,
                        notificationId + 2
                );

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel(manager);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_health)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .addAction(
                                R.drawable.ic_check,
                                getString(R.string.action_taken),
                                takenPendingIntent
                        )
                        .addAction(
                                R.drawable.ic_close,
                                getString(R.string.action_not_taken),
                                notTakenPendingIntent
                        );

        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    /**
     * Creates PendingIntent for notification actions.
     */
    private PendingIntent createActionPendingIntent(
            @NonNull String action,
            @NonNull String logId,
            @NonNull String status,
            int requestCode
    ) {

        Intent intent = new Intent(this, WearNotificationReceiver.class);
        intent.setAction(action);
        intent.putExtra(Constants.KEY_LOG_ID, logId);
        intent.putExtra(Constants.KEY_TAKEN_STATUS, status);

        return PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    /**
     * Creates notification channel.
     * Required for Wear OS notifications.
     */
    private void createNotificationChannel(NotificationManager manager) {
        if (manager == null) return;

        NotificationChannel channel = new NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
        );

        manager.createNotificationChannel(channel);
    }
}