package es.fonkyprojects.drivejob.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.activity.RideDetailActivity;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    //Object representing the message received from Firebase Cloud Messaging.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            int value = Integer.parseInt(remoteMessage.getData().get("value"));
            if(value == 0){ //User request
                notUserRequest(remoteMessage.getData().get("username"), remoteMessage.getData().get("key"));
            }
            else if(value == 1){ //User accept

            }
            else if(value == 2){ //User reject

            }
            else if(value == 3){ //User gone

            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

     //Create and show a simple notification containing the received FCM message.
    private void notUserRequest(String user, String key) {
        Intent intent = new Intent(this, RideDetailActivity.class);
        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, key);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String messageBody = getString(R.string.notUserRequestText, user);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_mylauncher)
                .setContentTitle(getString(R.string.notUserRequestTitle))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
