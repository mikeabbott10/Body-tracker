package it.unipi.sam.volleyballmovementtracker.util.bluetooth;

import android.bluetooth.BluetoothSocket;

import androidx.annotation.Nullable;

public interface OnConnectToBTServerListener {
    void onConnectionEstablished(BluetoothSocket bs);
    void onPermissionError( String msg);
    void onConnectionError(@Nullable BluetoothSocket skt);
}
