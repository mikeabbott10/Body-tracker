package it.unipi.sam.volleyballmovementtracker.util;

import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.unipi.sam.volleyballmovementtracker.util.download.OnBroadcastReceiverOnDMReceiveListener;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyBroadcasReceiver";
    private final OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener;
    private OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener;

    public MyBroadcastReceiver(OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener, OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener){
        this.onBTReceiveListener = onBTReceiveListener;
        this.onDMReceiveListener = onDMReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                onBTReceiveListener.onBluetoothStateChangedEventReceived(context, intent);
                break;
            }
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:{
                int scanMode = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
                Log.d(TAG, scanMode+"");
                onBTReceiveListener.onBluetoothScanModeChangedEventReceived(scanMode);
                /*if(scanMode==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    // device discoverable
                }else if(scanMode==BluetoothAdapter.SCAN_MODE_CONNECTABLE){
                    // device non discoverable ma può ricevere connessioni
                }else if(scanMode==BluetoothAdapter.SCAN_MODE_NONE){
                    // device non discoverbale e non può ricevere connessioni
                }*/
            }
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:{
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
                // qui gestiamo il completamento del download
                onDMReceiveListener.onDownloadCompleted(id);
            }
        }
    }
}
