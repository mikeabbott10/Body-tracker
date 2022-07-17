package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface OnFoundDeviceSelectedListener {
    void onDeviceSelected(BluetoothDevice btd);
}
