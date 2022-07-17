package it.unipi.sam.bodymovementtracker.util.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.DataWrapper;

public class ConnectedThread extends Thread {
    private static final String TAG = "CLCLConnectedThread";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler handler; // handler that gets info from Bluetooth service
    private final ObjectOutputStream oos;

    private boolean streamEndMessageAlreadySent;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        this.streamEndMessageAlreadySent = false;
        this.handler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        ObjectOutputStream oos1;
        try {
            oos1 = new ObjectOutputStream(mmOutStream);
        } catch (IOException e) {
            Log.w(TAG, "Not able to create ObjectOutputStream", e);
            oos1 = null;
        }
        oos = oos1;
    }

    public void run() {
        if(oos==null) {
            sendStreamEndedMsg();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(mmInStream)) {
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                //Log.d(TAG, "waiting for data");

                try {
                    DataWrapper sd = (DataWrapper) ois.readObject();
                    //Log.d(TAG, "received sd:" + sd);

                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            Constants.MESSAGE_READ, -1/*numBytes*/, -1,
                            sd);
                    readMsg.sendToTarget();
                } catch (ClassNotFoundException e1) {
                    Log.e(TAG, "", e1);
                    break;
                }
            }
        }catch (IOException e) {
            Log.w(TAG, "Input stream was disconnected", e);
            sendStreamEndedMsg();
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(Object data) {
        try {
            if(!streamEndMessageAlreadySent)
                oos.writeObject(data);
            // else connection has been stopped before
        }catch(IOException e){
            Log.e(TAG, "Error occurred when sending data", e);
            sendStreamEndedMsg();
            return;
        }
        //Log.d(TAG, "data sent");

        // Share the sent message with the UI activity.
        /*Message writtenMsg = handler.obtainMessage(
                Constants.MESSAGE_WRITE, -1, -1, mmBuffer);
        writtenMsg.sendToTarget();*/
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmInStream.close();
        } catch (IOException e) {
            Log.w(TAG, "Could not close the socket input stream", e);
        }
        try {
            mmOutStream.close();
        } catch (IOException e) {
            Log.w(TAG, "Could not close the socket output stream", e);
        }
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.w(TAG, "Could not close the connect socket", e);
        }
    }

    // utils ---------------------------------------------------------------------------------------
    private void sendStreamEndedMsg(){
        if(!streamEndMessageAlreadySent){
            // Send a disconnection message back to the activity.
            Message writtenMsg = handler.obtainMessage(
                    Constants.STREAM_DISCONNECTED, -1, -1, null);
            writtenMsg.sendToTarget();
            streamEndMessageAlreadySent = true;
        }
    }
}
