package it.unipi.sam.volleyballmovementtracker.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import it.unipi.sam.volleyballmovementtracker.activities.util.CommonPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class ServiceCommunicationActivity extends CommonPracticingActivity {
    private static final String TAG = "AAAAServiceCommActiv";
    protected ServiceConnection mConnection;
    protected boolean mShouldUnbind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShouldUnbind = false;
        if(savedInstanceState!=null)
            mShouldUnbind = savedInstanceState.getBoolean(Constants.should_unbind_key, false);

        if(NotificationManagerCompat.from(this).areNotificationsEnabled()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(isChannelBlocked(Constants.NOTIFICATION_CHANNEL_ID)){
                    askForNotificationChannelSettings();
                }
            }
        }else{
            askForNotificationSettings();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.should_unbind_key, mShouldUnbind);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNotificationChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    // service communication -----------------------------------------------------------------------
    protected void doStartService(int role){
        Intent inte = new Intent(getApplicationContext(), BluetoothService.class);
        inte.putExtra(Constants.choice_key, role);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(inte);
        }else{
            startService(inte);
        }
    }
    protected void doBindService(Class<? extends Service> clas) {
        if(mShouldUnbind)
            return;
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(new Intent(this, clas),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e(TAG, "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    void doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    // notification --------------------------------------------------------------------------------
    private void askForNotificationSettings() {
        showMyDialog(Constants.NOTIFICATION_ENABLING_DIALOG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void askForNotificationChannelSettings() {
        showMyDialog(Constants.NOTIFICATION_CHANNEL_ENABLING_DIALOG);
    }

    /**
     *  Notification channel.
     */
    protected void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                                                    Constants.NOTIFICATION_CHANNEL_NAME, importance);
            //channel.setDescription(description);
            channel.setSound(null,null);
            channel.enableVibration(false);
            // Register the channel with the system;
            // can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     *  Open OS notification settings.
     */
    private void openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * Check if the notification channel is blocked.
     *
     * @param channelId : the channel ID to check.
     * @return boolean : true if the channel is blocked.
     */
    @RequiresApi(26)
    private boolean isChannelBlocked(String channelId) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = manager.getNotificationChannel(channelId);
        return channel != null &&
                channel.getImportance() == NotificationManager.IMPORTANCE_NONE;
    }

    /**
     * Open OS notification channel settings.
     *
     * @param channelId : the channel ID.
     */
    @RequiresApi(26)
    private void openChannelSettings(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        startActivity(intent);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface, i);
        if (showingDialog == Constants.NOTIFICATION_ENABLING_DIALOG){
            openNotificationSettings();
        } else if(showingDialog == Constants.NOTIFICATION_CHANNEL_ENABLING_DIALOG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isChannelBlocked(Constants.NOTIFICATION_CHANNEL_ID)) {
                    openChannelSettings(Constants.NOTIFICATION_CHANNEL_ID);
                }
            }
        }
    }
}
