package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothSocket;

public interface OnConnectToBTServerListener {
    void onConnectionEstablished(BluetoothSocket bs);
    void onPermissionError( String msg);
}
