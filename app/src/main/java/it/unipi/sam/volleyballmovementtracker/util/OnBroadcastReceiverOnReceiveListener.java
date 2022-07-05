package it.unipi.sam.volleyballmovementtracker.util;

import android.content.Context;
import android.content.Intent;

public interface OnBroadcastReceiverOnReceiveListener {
    void onBluetoothStateChangedEventReceived(Context context, Intent intent);
    void onBluetoothScanModeChangedEventReceived(int scanMode);
}
