package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothSocket;

public interface OnGetBTConnectionsListener {
    void onNewConnectionEstablished(BluetoothSocket bs);
}
