package it.unipi.sam.volleyballmovementtracker.util;

import android.app.DownloadManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.download.OnBroadcastReceiverOnDMReceiveListener;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyBroadcReceiv";
    private final OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener;
    private final OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener;

    public MyBroadcastReceiver(OnBroadcastReceiverOnBTReceiveListener onBTReceiveListener,
                               OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener){
        this.onBTReceiveListener = onBTReceiveListener;
        this.onDMReceiveListener = onDMReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            // BT ----------------------------------------------------------------------------------
            case BluetoothAdapter.ACTION_STATE_CHANGED: {
                onBTReceiveListener.onBluetoothStateChangedEventReceived(
                        intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE, -1));
                break;
            }
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED: {
                int scanMode = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
                Log.d(TAG, scanMode + "");
                onBTReceiveListener.onBluetoothScanModeChangedEventReceived(scanMode);
                break;
            }
            case BluetoothDevice.ACTION_FOUND: {
                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, dev + "");
                onBTReceiveListener.onBluetoothActionFoundEventReceived(dev);
                break;
            }
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:{
                onBTReceiveListener.onBluetoothActionDiscoveryEventReceived(true);
                break;
            }
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:{
                onBTReceiveListener.onBluetoothActionDiscoveryEventReceived(false);
                break;
            }
            // DM ----------------------------------------------------------------------------------
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE: {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                // qui gestiamo il completamento del download
                if(onDMReceiveListener!=null)
                    onDMReceiveListener.onDownloadCompleted(id);
                break;
            }
        }
    }
}
