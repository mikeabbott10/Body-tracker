package it.unipi.sam.bodymovementtracker.services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.MyNotificationBroadcastReceiver;

public class NotificationService extends BaseService {
    private static String TAG = "SSSSNotificService";

    // notification
    int NOTIFICATION_ID = (int) (System.currentTimeMillis()%1000);
    NotificationCompat.Builder notification;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myRole = intent.getIntExtra(Constants.choice_key, -1);
        if(myRole == Constants.COACH_CHOICE)
            startNotifying((String) getText(R.string.coach_state_none_notification), R.drawable.coach1);
        else
            startNotifying((String) getText(R.string.player_state_none_notification), R.drawable.block3);
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    // overridden
    @Nullable @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    protected void startNotifying(String message, int notificationIcon){
        Intent closeServiceIntent =
                new Intent(this, MyNotificationBroadcastReceiver.class);
        closeServiceIntent.setAction(MyNotificationBroadcastReceiver.CLOSETHESERVICE);

        PendingIntent closeServicePendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            closeServicePendingIntent = PendingIntent.getBroadcast(
                    this, 0, closeServiceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            closeServicePendingIntent = PendingIntent.getBroadcast(this, 0,
                    closeServiceIntent, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {       // Oreo and after
            NotificationCompat.Action closeService = new NotificationCompat.Action(
                    R.drawable.ic_close,
                    getText(R.string.close),
                    closeServicePendingIntent);
            notification = new NotificationCompat.Builder(
                    this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(message)
                    .setSmallIcon(notificationIcon)
                    .addAction(closeService)
                    ;
            // notificationId is a unique int for each notification that you must define
            startForeground(NOTIFICATION_ID, notification.build());
        } else {    // before Oreo
            NotificationCompat.Action closeService = new NotificationCompat.Action.Builder(
                    R.drawable.ic_close,
                    getText(R.string.close),
                    closeServicePendingIntent)
                    .build();
            notification = new NotificationCompat.Builder(
                    this, Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(message)
                    .setSmallIcon(ResourcesCompat.ID_NULL)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSound(null)
                    .setVibrate(null)
                    .addAction(closeService)
                    ;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            try{
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }catch (IllegalArgumentException e){
                Log.w(TAG, "", e);
                notification.setSmallIcon(notificationIcon);
                notificationManager.notify(NOTIFICATION_ID, notification.build());
            }
        }
    }
}