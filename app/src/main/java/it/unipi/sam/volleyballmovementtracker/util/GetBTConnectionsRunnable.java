package it.unipi.sam.volleyballmovementtracker.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

public class GetBTConnectionsRunnable implements Runnable {
    private final Handler mHandler;
    private final BluetoothAdapter bta;

    public GetBTConnectionsRunnable(BluetoothAdapter bta, OnGetBTConnectionsListener onGetBTConnectionsListener) {
        this.bta = bta;
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                onGetBTConnectionsListener.onNewConnectionEstablished(
                        (BluetoothSocket) msg.obj );
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        UUID myid = UUID.fromString(Constants.BT_UUID);
        try(BluetoothServerSocket bss = bta.listenUsingRfcommWithServiceRecord(Constants.BT_NAME, myid)) {
            BluetoothSocket bs = bss.accept();
            sendMessage(bs);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendMessage(Object obj){
        Message m = new Message();
        m.obj = obj;
        mHandler.sendMessage(m);
    }
}
