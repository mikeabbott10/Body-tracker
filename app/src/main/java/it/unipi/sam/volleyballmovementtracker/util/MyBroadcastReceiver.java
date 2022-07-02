package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private OnBroadcastReceiverOnReceiveListener onReceiveListener;

    public MyBroadcastReceiver(OnBroadcastReceiverOnReceiveListener onReceiveListener){
        this.onReceiveListener = onReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            onReceiveListener.onBluetoothEventReceived(context, intent);
        }
    }
}
