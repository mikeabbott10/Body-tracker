package it.unipi.sam.volleyballmovementtracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BaseService extends Service {
    protected boolean isRunning;
    protected int handleRestartCode = START_NOT_STICKY;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        return handleRestartCode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
