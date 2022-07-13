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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import it.unipi.sam.volleyballmovementtracker.util.BtAndServiceStatesWrapper;
import it.unipi.sam.volleyballmovementtracker.util.ConnectToBTServerRunnable;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.GetBTConnectionsRunnable;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
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
    public int role;

    // binder
    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    // communication service to activity
    BtAndServiceStatesWrapper thisServiceState, thisBtState;
    private MutableLiveData<BtAndServiceStatesWrapper> live_state;
    public MutableLiveData<BtAndServiceStatesWrapper> getLive_state() {
        return live_state;
    }

    @Override
    public void onCreate() {
        Log.i( TAG, "onCreate");
        super.onCreate();
        live_state = new MutableLiveData<>();
        thisServiceState = new BtAndServiceStatesWrapper(-1,-1);
        thisBtState = new BtAndServiceStatesWrapper(-1,-1);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i( TAG, "onStartCommand");
        if(!isStarted) {
            role = intent.getIntExtra(Constants.choice_key, -1);
            bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            updateServiceState(Constants.STARTING_SERVICE);
            if(!bta.isEnabled()){
                updateBTState(Constants.BT_STATE_DISABLED);
            }else{
                updateBTState(Constants.BT_STATE_ENABLED);
            }
            mReceiver = new MyBroadcastReceiver(this, null);
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
    public IBinder onBind(@NonNull Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i( TAG, "onDestroy");
        if(getBTConnectionsRunnable!=null)
            getBTConnectionsRunnable.cancel();
        if(connectToBTServerRunnable!=null)
            connectToBTServerRunnable.cancel();
        try{unregisterReceiver(mReceiver);}catch (RuntimeException ignored){}
        super.onDestroy();
    }

    @Override
    public void myStop() {
        updateServiceState(Constants.CLOSING_SERVICE);
        super.myStop();
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    @Override
    public void onBluetoothStateChangedEventReceived(int state) {
        switch(state){
            case BluetoothAdapter.STATE_OFF:{
                myStop();
                break;
            }
        }
    }

    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {
        switch(scanMode){
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:{
                // device discoverable
                break;
            }
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE: {
                // device non discoverable ma può ricevere connessioni
                if(thisBtState.getBtState() == Constants.BT_STATE_DISCOVERABLE){
                    assert role == Constants.COACH_CHOICE;
                    // still waiting for client
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
                break;
            }
            case BluetoothAdapter.SCAN_MODE_NONE:{
                // device non discoverbale e non può ricevere connessioni
                if(thisBtState.getBtState() == Constants.BT_STATE_DISCOVERABLE){
                    assert role == Constants.COACH_CHOICE;
                    // still waiting for client
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
            }
        }
    }

    // unused here
    @Override public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {}

    // OnConnectToBTServerListener ------------------------------------------------------------------
    // (player callbacks)
    @Override
    public void onConnectionEstablished(BluetoothSocket bs) {
        Log.d(TAG, "coach bs: "+bs);
        updateBTState(Constants.BT_STATE_CONNECTED);
    }

    @Override
    public void onPermissionError(String obj) {
        // permissions removed during service life
        updateBTState(Constants.BT_STATE_BADLY_DENIED);
        stopSelf();
    }

    // OnGetBTConnectionsListener ------------------------------------------------------------------
    // (coach callbacks)
    @Override
    public void onNewConnectionEstablished(BluetoothSocket bs) {
        // A connection was accepted. Perform work associated with
        // the connection in a separate thread.
        Log.d(TAG, "new bs: "+bs);
        updateBTState(Constants.BT_STATE_CONNECTED);
        // todo: che si fa?? si va a letto?
        try {
            bs.close();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void onBTError(String msg) {
        Log.e(TAG, "onBTError msg: "+ msg);
        updateBTState(Constants.BT_STATE_UNSOLVED);
        stopSelf();
    }

    // utils ---------------------------------------------------------------------------------------
    /**
     * Called from bound activity after bt got enabled
     */
    public void onMeEnabled() {
        updateBTState(Constants.BT_STATE_ENABLED);
    }

    protected void updateBTState(int btState) {
        thisBtState.setBtState(btState);
        live_state.setValue(thisBtState);
    }

    private void updateServiceState(int serviceState) {
        thisServiceState.setServiceState(serviceState);
        live_state.setValue(thisServiceState);
    }
}
