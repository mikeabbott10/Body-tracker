package it.unipi.sam.volleyballmovementtracker.services;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SensorService extends BluetoothService{
    private static final String TAG = "SSSSSensorService";

    // binder
    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Nullable @Override
    public IBinder onBind(@NonNull Intent intent) {
        return mBinder;
    }

    @Override
    protected void startCollectingData() {
        initSensor();
    }

    private void initSensor(){
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

        for (Sensor sens : list) {
            Log.d(TAG, sens.toString());
        }
/*
        //Registra l per essere informato degli eventi relativi
        // a sens, con una frequenza di circa rate
        boolean success = sm.registerListener(
                SensorEventListener l,
                Sensor sens, // sensor
                SensorManager.SENSOR_DELAY_UI // rate
        );

 */
    }
}
