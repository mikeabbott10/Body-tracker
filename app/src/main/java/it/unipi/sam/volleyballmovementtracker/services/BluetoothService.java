package it.unipi.sam.volleyballmovementtracker.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import it.unipi.sam.volleyballmovementtracker.util.ConnectToBTServerRunnable;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.GetBTConnectionsRunnable;
import it.unipi.sam.volleyballmovementtracker.util.MyBTBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.OnConnectToBTServerListener;
import it.unipi.sam.volleyballmovementtracker.util.OnGetBTConnectionsListener;

public class BluetoothService extends NotificationService implements OnGetBTConnectionsListener,
        OnConnectToBTServerListener, OnBroadcastReceiverOnBTReceiveListener {
    private static String TAG = "SSSSBluetoothService";
    protected BluetoothAdapter bta;
    protected BroadcastReceiver mReceiver;
    protected GetBTConnectionsRunnable getBTConnectionsRunnable;
    protected ConnectToBTServerRunnable connectToBTServerRunnable;

    // binder
    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    // communication service to activity
    private MutableLiveData<Integer> bt_live_state;
    public MutableLiveData<Integer> getBt_live_state() {
        return bt_live_state;
    }

    @Override
    public void onCreate() {
        Log.i( TAG, "onCreate");
        super.onCreate();
        bt_live_state = new MutableLiveData<>();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isRunning) {
            bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            if(!bta.isEnabled()){
                bt_live_state.setValue(Constants.BT_STATE_DISABLED);
            }else{
                bt_live_state.setValue(Constants.BT_STATE_ENABLED);
            }
            mReceiver = new MyBTBroadcastReceiver(this);
            IntentFilter intentFilters = new IntentFilter();
            intentFilters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilters.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(mReceiver, intentFilters);
            return super.onStartCommand(intent, flags, startId);
        }else {
            return handleRestartCode;
        }
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if(getBTConnectionsRunnable!=null)
            getBTConnectionsRunnable.cancel();
        if(connectToBTServerRunnable!=null)
            connectToBTServerRunnable.cancel();
        try{unregisterReceiver(mReceiver);}catch (RuntimeException ignored){}
        super.onDestroy();
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    @Override
    public void onBluetoothStateChangedEventReceived(int state) {

    }

    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {

    }

    // unused here
    @Override public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {}

    // OnConnectToBTServerListener ------------------------------------------------------------------
    // (player callbacks)
    @Override
    public void onConnectionEstablished(BluetoothSocket bs) {
        Log.d(TAG, "coach bs: "+bs);
        bt_live_state.setValue(Constants.BT_STATE_CONNECTED);
    }

    @Override
    public void onPermissionError(String obj) {
        // permissions removed during service life
        bt_live_state.setValue(Constants.BT_STATE_BADLY_DENIED);
        stopSelf();
    }

    // OnGetBTConnectionsListener ------------------------------------------------------------------
    // (coach callbacks)
    @Override
    public void onNewConnectionEstablished(BluetoothSocket bs) {
        // A connection was accepted. Perform work associated with
        // the connection in a separate thread.
        Log.d(TAG, "new bs: "+bs);
        bt_live_state.setValue(Constants.BT_STATE_CONNECTED);
        // todo: che si fa?? si va al mare?
        try {
            bs.close();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void onBTError(String msg) {
        Log.e(TAG, "onBTError msg: "+ msg);
        bt_live_state.setValue(Constants.BT_STATE_UNSOLVED);
        stopSelf();
    }

    // utils ---------------------------------------------------------------------------------------
    /**
     * Called from bound coach activity after bt got discoverable
     */
    public void onMeDiscoverable() {
        bt_live_state.setValue(Constants.BT_STATE_DISCOVERABLE);
        // get this listening for incoming connections
        getBTConnectionsRunnable = new GetBTConnectionsRunnable(this, bta, this);
        new Thread(getBTConnectionsRunnable).start();
    }

    /**
     * Called from bound player activity after user manually selected the device of the coach
     * @param btDevice the device we are gonna connect to
     */
    public void onBTServerSelected(BluetoothDevice btDevice){
        // get this listening for incoming connections
        connectToBTServerRunnable = new ConnectToBTServerRunnable(this, btDevice, bta, this);
        new Thread(connectToBTServerRunnable).start();
    }

}
