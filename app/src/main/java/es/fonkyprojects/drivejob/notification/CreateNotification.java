package es.fonkyprojects.drivejob.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.activity.RideDetailActivity;
import es.fonkyprojects.drivejob.restMethods.DeleteTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.MyApp;

public class CreateNotification {

    public CreateNotification() {
    }

    public void select(String userIdTo, String username, String key, int value){
            String title = "";
            String body = "";
            if(value == 0){ //User request
                title = MyApp.getAppContext().getString(R.string.notUserRequestTitle);
                body = MyApp.getAppContext().getString(R.string.notUserRequestText, username);
            } else if(value == 10){ //Accept join
                title = MyApp.getAppContext().getString(R.string.notAcceptJoinTitle);
                body = MyApp.getAppContext().getString(R.string.notAcceptJointText, username);
            } else if(value == 20){ //Refuse join
                title = MyApp.getAppContext().getString(R.string.notRefuseJoinTitle);
                body = MyApp.getAppContext().getString(R.string.notRefuseJoinText, username);
            } else if(value == 30){ //Kick join
                title = MyApp.getAppContext().getString(R.string.notKickJoinTitle);
                body = MyApp.getAppContext().getString(R.string.notKickJoinText, username);
            } else if(value == 40) { //Exit ride
                title = MyApp.getAppContext().getString(R.string.notExitRideTitle);
                body = MyApp.getAppContext().getString(R.string.notExitRideText, username);
            } else if(value == 50) { //Edit Ride
                title = MyApp.getAppContext().getString(R.string.notEditRideTitle);
                body = MyApp.getAppContext().getString(R.string.notEditRideText, username);
            } else if(value == 60) { //Delete Ride
                title = MyApp.getAppContext().getString(R.string.notDeleteRideTitle);
                body = MyApp.getAppContext().getString(R.string.notDeleteRideText, username);
            }

        try {
            String result = new DeleteTask(MyApp.getAppContext()).execute(Constants.BASE_URL + "message/?useridTo=" + userIdTo).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        notificationRide(key, title, body);
    }

    //Create and show a simple notification containing the received FCM message.
    private void notificationRide(String key, String title, String body) {
        Intent intent = new Intent(MyApp.getAppContext(), RideDetailActivity.class);
        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, key);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApp.getAppContext(), 0, intent,
        PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyApp.getAppContext())
        .setSmallIcon(R.drawable.ic_logo_not)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
        (NotificationManager) MyApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Random rand = new Random();
        int i = rand.nextInt(999 + 1);
        notificationManager.notify(i, notificationBuilder.build());
    }
}
