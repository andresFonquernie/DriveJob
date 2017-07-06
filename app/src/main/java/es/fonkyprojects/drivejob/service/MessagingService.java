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
            String title = "";
            String body = "";
            if(value == 0){ //User request
                title = getString(R.string.notUserRequestTitle);
                body = getString(R.string.notUserRequestText, remoteMessage.getData().get("username"));
            } else if(value == 10){ //Refuse join
                title = getString(R.string.notRefuseJoinTitle);
                body = getString(R.string.notRefuseJoinText, remoteMessage.getData().get("username"));
            } else if(value == 20){ //Accept join
                title = getString(R.string.notAcceptJoinTitle);
                body = getString(R.string.notAcceptJointText, remoteMessage.getData().get("username"));
            } else if(value == 30){ //Kick join
                title = getString(R.string.notKickJoinTitle);
                body = getString(R.string.notKickJoinText, remoteMessage.getData().get("username"));
            } else if(value == 40) { //Exit ride
                title = getString(R.string.notExitRideTitle);
                body = getString(R.string.notExitRideText, remoteMessage.getData().get("username"));
            } else if(value == 50) { //Edit Ride
                title = getString(R.string.notEditRideTitle);
                body = getString(R.string.notEditRideText, remoteMessage.getData().get("username"));
            } else if(value == 60) { //Delete Ride
                title = getString(R.string.notDeleteRideTitle);
                body = getString(R.string.notDeleteRideText, remoteMessage.getData().get("username"));
            }
            notificationRide(remoteMessage.getData().get("username"), remoteMessage.getData().get("key"), title, body);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

     //Create and show a simple notification containing the received FCM message.
    private void notificationRide(String user, String key, String title, String body) {
        Log.e(TAG, "Key = " + key);
        Intent intent = new Intent(this, RideDetailActivity.class);
        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, key);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_not)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
