package it.unipi.sam.volleyballmovementtracker.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;

public class MyNotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyNotifBroadcRece";

    public static final String CLOSETHESERVICE = "it.unipi.sam.volleyballmovementtracker.broadcast.CLOSETHEAPP";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch(action){
            case CLOSETHESERVICE:{
                Intent myServiceIntent = new Intent(context, BluetoothService.class);
                IBinder service = this.peekService(context, myServiceIntent);
                if(service==null) break;
                BluetoothService mBoundService = ((BluetoothService.LocalBinder)service).getService();
                mBoundService.myStop();
                break;
            }
        }
    }
}