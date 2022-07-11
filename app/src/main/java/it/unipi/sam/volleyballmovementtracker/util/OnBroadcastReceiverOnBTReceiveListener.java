package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothDevice;

public interface OnBroadcastReceiverOnBTReceiveListener {
    void onBluetoothStateChangedEventReceived(int state);
    void onBluetoothScanModeChangedEventReceived(int scanMode);
    void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice);
}