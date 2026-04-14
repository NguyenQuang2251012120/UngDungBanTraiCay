package com.example.ungdungbantraicay.Service;

import android.app.NotificationManager;

import androidx.core.app.NotificationCompat;

import com.example.ungdungbantraicay.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = "Test";
        String body = "Hello";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "order_channel")
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(1, builder.build());
    }
}
