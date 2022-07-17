package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import it.unipi.sam.bodymovementtracker.util.Constants;

public class GetBTConnectionsRunnable implements Runnable {
    private static final String TAG = "CLCLGetBTConnRunnab";
    private final Handler mHandler;
    private BluetoothServerSocket mBTSSocket;
    private final int NEW_CONNECTION = 0;
    private final int PERMISSION_ERROR = 1;
    private final int BT_ERROR = 2;

    public GetBTConnectionsRunnable(Context ctx, BluetoothAdapter bta, OnGetBTConnectionsListener onGetBTConnectionsListener) {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "arg1:"+msg.arg1+", obj:"+msg.obj);
                if(msg.arg1 == NEW_CONNECTION)
                    onGetBTConnectionsListener.onNewConnectionEstablished(
                            (BluetoothSocket) msg.obj);
                else if(msg.arg1 == PERMISSION_ERROR)
                    onGetBTConnectionsListener.onPermissionError(
                            (String) msg.obj);
                else if(msg.arg1 == BT_ERROR)
                    onGetBTConnectionsListener.onBTError(
                            (String) msg.obj);
            }
        };

        try {
            UUID myid = UUID.fromString(Constants.BT_UUID);
            mBTSSocket = bta.listenUsingRfcommWithServiceRecord(Constants.BT_NAME, myid);
        } catch (IOException e) {
            Log.e(TAG, "", e);
            sendMessage(BT_ERROR, e.getMessage());
            mBTSSocket = null;
        }catch (SecurityException e){
            Log.e(TAG, "", e);
            sendMessage(PERMISSION_ERROR, e.getMessage());
            mBTSSocket = null;
        }
    }

    @Override
    public void run() {
        if(mBTSSocket==null)
            return;

        // RFCOMM allows only one connected client per channel at a time, so
        // in most cases it makes sense to call close() on the BluetoothServerSocket
        // immediately after accepting a connected socket.
        BluetoothSocket socket;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mBTSSocket.accept();
            } catch (IOException e) {
                Log.w(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                cancel();
                sendMessage(NEW_CONNECTION, socket);
                break;
            }
        }
    }

    private void sendMessage(int arg1, Object obj){
        Message m = new Message();
        m.arg1 = arg1;
        m.obj = obj;
        mHandler.sendMessage(m);
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mBTSSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
