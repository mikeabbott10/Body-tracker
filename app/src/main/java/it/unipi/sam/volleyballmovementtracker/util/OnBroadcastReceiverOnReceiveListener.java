package it.unipi.sam.volleyballmovementtracker.util;

import android.content.Context;
import android.content.Intent;

public interface OnBroadcastReceiverOnReceiveListener {
    void onBluetoothEventReceived(Context context, Intent intent);
}
