package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyBroadcasReceiver";
    private OnBroadcastReceiverOnReceiveListener onReceiveListener;

    public MyBroadcastReceiver(OnBroadcastReceiverOnReceiveListener onReceiveListener){
        this.onReceiveListener = onReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                onReceiveListener.onBluetoothStateChangedEventReceived(context, intent);
                break;
            }
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:{
                int scanMode = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
                Log.d(TAG, scanMode+"");
                onReceiveListener.onBluetoothScanModeChangedEventReceived(scanMode);
                if(scanMode==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    // device discoverable
                }else if(scanMode==BluetoothAdapter.SCAN_MODE_CONNECTABLE){
                    // device non discoverable ma può ricevere connessioni
                }else if(scanMode==BluetoothAdapter.SCAN_MODE_NONE){
                    // device non discoverbale e non può ricevere connessioni
                }
            }
        }
    }
}
