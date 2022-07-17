package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface OnBroadcastReceiverOnBTReceiveListener {
    void onBluetoothStateChangedEventReceived(int state);
    void onBluetoothScanModeChangedEventReceived(int scanMode);
    void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice);
    void onBluetoothActionDiscoveryEventReceived(boolean isDiscoveryFinished);
}