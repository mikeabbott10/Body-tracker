package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothSocket;

public interface OnGetBTConnectionsListener {
    void onNewConnectionEstablished(BluetoothSocket bs);
    void onPermissionError(String obj);
    void onBTError(String msg);
}
