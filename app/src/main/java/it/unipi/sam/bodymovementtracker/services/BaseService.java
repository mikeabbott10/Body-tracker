package it.unipi.sam.bodymovementtracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseService extends Service {
    public boolean isStarted;
    protected int handleRestartCode = START_NOT_STICKY;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        isStarted = true;
        return handleRestartCode;
    }

    @Override
    public void onDestroy() {
        isStarted = false;
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    public void myStop() {
        stopSelf();
    }
}
