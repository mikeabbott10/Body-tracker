package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyBTBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyBTBroadcReceiv";
    private final OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener;

    public MyBTBroadcastReceiver(OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener){
        this.onBTReceiveListener = onBTReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                onBTReceiveListener.onBluetoothStateChangedEventReceived(
                        intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1));
                break;
            }
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED: {
                int scanMode = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
                Log.d(TAG, scanMode + "");
                onBTReceiveListener.onBluetoothScanModeChangedEventReceived(scanMode);
            }
            case BluetoothDevice.ACTION_FOUND: {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, dev + "");
                onBTReceiveListener.onBluetoothActionFoundEventReceived(dev);
            }
        }
    }
}
