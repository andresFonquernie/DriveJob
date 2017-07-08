package es.fonkyprojects.drivejob.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.fonkyprojects.drivejob.notification.CreateNotification;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    //Object representing the message received from Firebase Cloud Messaging.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            CreateNotification c = new CreateNotification();
            int value = Integer.parseInt(remoteMessage.getData().get("value"));
            c.select(FirebaseUser.getUid(), remoteMessage.getData().get("usernameFrom"), remoteMessage.getData().get("key"), value);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
