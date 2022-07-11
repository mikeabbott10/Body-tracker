package it.unipi.sam.volleyballmovementtracker.services;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class NotificationService extends BaseService {
    private static String TAG = "SSSSNotificService";

    // notification
    int NOTIFICATION_ID = (int) (System.currentTimeMillis()%1000);
    NotificationCompat.Builder notification;

    @Override
    public void onCreate() {
        Log.i( TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myRole = intent.getIntExtra(Constants.choice_key, -1);
        if(myRole == Constants.COACH_CHOICE)
            startNotifying((String) getText(R.string.coach_state_none_notification));
        else
            startNotifying((String) getText(R.string.player_state_none_notification));
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        Log.i( TAG, "onDestroy");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    // overridden
    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startNotifying(String message){
        /*Intent toTheAppIntent = new Intent(this, MyBroadcastReceiver.class);
        toTheAppIntent.setAction(MyBroadcastReceiver.TOTHEAPP);

        PendingIntent toTheAppPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toTheAppPendingIntent = PendingIntent.getBroadcast(
                    this, 0, toTheAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            toTheAppPendingIntent = PendingIntent.getBroadcast(this, 0, toTheAppIntent, 0);
        }*/

        /*Intent smoothHandler = new Intent(this, MyBroadcastReceiver.class);
        smoothHandler.setAction(MyBroadcastReceiver.SMOOTH);
        PendingIntent smoothHandlerPendingIntent = PendingIntent.getBroadcast(this, 0, smoothHandler, 0);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // Oreo and after
            //NotificationCompat.Action toTheApp = new NotificationCompat.Action(R.drawable.ic_launcher_foreground, getText(R.string.to_the_app), toTheAppPendingIntent);
            notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    //.addAction(toTheApp)
                    ;
            startForeground(NOTIFICATION_ID, notification.build());
        } else {    // before Oreo
            //NotificationCompat.Action toTheApp = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, getText(R.string.to_the_app), toTheAppPendingIntent).build();
            notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSound(null)
                    .setVibrate(null)
                    //.addAction(toTheApp)
                    ;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
    }
}