package it.unipi.sam.volleyballmovementtracker.services;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.DataCollectionListener;

public class SensorService extends BluetoothService implements SensorEventListener{
    private static final String TAG = "SSSSSensorService";
    private SensorManager sm;
    private Sensor mAccelerometer;
    private DataCollectionListener dataCollectionListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isStarted) {
            role = intent.getIntExtra(Constants.choice_key, -1);
            if(role == Constants.PLAYER_CHOICE) {
                sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                    mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                } else{
                    // Sorry, there are no accelerometers on your device.
                    // You can't play this game.
                    myStop();
                    return handleRestartCode;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

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
    protected boolean startCollectingData(DataCollectionListener dataCollectionListener) {
        //Registra l per essere informato degli eventi relativi
        // a mySensor, con una frequenza di circa rate
        this.dataCollectionListener = dataCollectionListener;
        return sm.registerListener( this, mAccelerometer,SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    protected void stopCollectingData() {
        // deregistra listener
        try{sm.unregisterListener(this);}catch(Exception ignored){}
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged:"+ sensorEvent);
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        // normalize
        int accelerationCurrentValue = (int) Math.sqrt(x * x + y * y + z * z);
        //double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
        //accelerationPreviousValue = accelerationCurrentValue;
        dataCollectionListener.onNewDataCollected(accelerationCurrentValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged");
    }
}
