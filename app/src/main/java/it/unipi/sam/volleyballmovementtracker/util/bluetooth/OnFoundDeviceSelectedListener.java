package it.unipi.sam.volleyballmovementtracker.util.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface OnFoundDeviceSelectedListener {
    void onDeviceSelected(BluetoothDevice btd);
}
