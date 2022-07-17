package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

public class ConnectToBTServerRunnable implements Runnable {
    private static final String TAG = "CLCLConnToBTSerRunnab";
    private final Context ctx;
    private BluetoothAdapter bta;
    private final Handler mHandler;
    private BluetoothSocket mmSocket;

    private static final int NEW_CONNECTION = 0;
    private static final int PERMISSION_ERROR = 1;
    private static final int CONNECTION_ERROR = 2;

    public ConnectToBTServerRunnable(Context ctx, BluetoothDevice btDevice, BluetoothAdapter bta, OnConnectToBTServerListener onConnectToBTServerListener) {
        this.ctx = ctx;
        this.bta = bta;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == NEW_CONNECTION)
                    onConnectToBTServerListener.onConnectionEstablished(
                            (BluetoothSocket) msg.obj);
                else if (msg.arg1 == PERMISSION_ERROR)
                    onConnectToBTServerListener.onPermissionError(
                            (String) msg.obj);
                else if (msg.arg1 == CONNECTION_ERROR)
                    onConnectToBTServerListener.onConnectionError(
                            (BluetoothSocket) msg.obj);

            }
        };

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            UUID myid = UUID.fromString(Constants.BT_UUID);
            mmSocket = btDevice.createRfcommSocketToServiceRecord(myid);
        } catch (SecurityException e) {
            Log.e(TAG, "", e);
            sendMessage(PERMISSION_ERROR, e.getMessage());
            mmSocket = null;
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
            sendMessage(CONNECTION_ERROR, e.getMessage());
            mmSocket = null;
        }
    }

    @Override
    public void run() {
        if(mmSocket==null) {
            sendMessage(CONNECTION_ERROR, null);
            return;
        }
        try {
            // Cancel discovery because it otherwise slows down the connection.
            bta.cancelDiscovery();
        } catch (SecurityException e) {
            Log.e(TAG, "", e);
            sendMessage(PERMISSION_ERROR, e.getMessage());
            return;
        }

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        }catch (SecurityException e){
            Log.e(TAG, "no permissions", e);
            sendMessage(PERMISSION_ERROR, e.getMessage());
            cancel();
            return;
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG, "unable to connect", connectException);
            cancel();
            sendMessage(CONNECTION_ERROR, mmSocket);
            return;
        }

        sendMessage(NEW_CONNECTION, mmSocket);
    }

    private void sendMessage(int arg1, Object obj){
        Message m = new Message();
        m.arg1 = arg1;
        m.obj = obj;
        mHandler.sendMessage(m);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.w(TAG, "Could not close the client socket", e);
        }
    }
}
