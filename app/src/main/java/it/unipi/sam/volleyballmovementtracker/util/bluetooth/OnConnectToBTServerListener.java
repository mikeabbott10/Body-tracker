package it.unipi.sam.volleyballmovementtracker.util.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface OnConnectToBTServerListener {
    void onConnectionEstablished(BluetoothSocket bs);
    void onPermissionError( String msg);
}
