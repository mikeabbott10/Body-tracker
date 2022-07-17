package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface OnGetBTConnectionsListener {
    void onNewConnectionEstablished(BluetoothSocket bs);
    void onPermissionError(String obj);
    void onBTError(String msg);
}
