package it.unipi.sam.bodymovementtracker.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.Set;

import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.DataCollectionListener;
import it.unipi.sam.bodymovementtracker.util.MessageWrapper;
import it.unipi.sam.bodymovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.bodymovementtracker.util.DataWrapper;
import it.unipi.sam.bodymovementtracker.util.bluetooth.ConnectToBTServerRunnable;
import it.unipi.sam.bodymovementtracker.util.bluetooth.ConnectedThread;
import it.unipi.sam.bodymovementtracker.util.bluetooth.GetBTConnectionsRunnable;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnConnectToBTServerListener;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnGetBTConnectionsListener;

public abstract class BluetoothService extends NotificationService implements OnGetBTConnectionsListener,
        OnConnectToBTServerListener, OnBroadcastReceiverOnBTReceiveListener, DataCollectionListener {
    private static String TAG = "SSSSBluetoothService";
    protected BluetoothAdapter bta;
    protected BroadcastReceiver mReceiver;
    protected GetBTConnectionsRunnable getBTConnectionsRunnable;
    protected ConnectToBTServerRunnable connectToBTServerRunnable;
    public int role;
    protected DataWrapper dw;
    private boolean isGetBTConnectionsThreadRunning;
    private boolean isConnectToBTServerThreadRunning;

    public ConnectedThread connectedDeviceThread;
    private final Handler connectedDeviceHandler = new Handler(Looper.getMainLooper()) {
        MessageWrapper mw = new MessageWrapper(null, true);
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == Constants.STREAM_DISCONNECTED){
                updateBTState(Constants.BT_STATE_JUST_DISCONNECTED);
                if(role==Constants.PLAYER_CHOICE)
                    stopCollectingData();
                connectedDeviceThread.cancel();
                return;
            }
            mw.msg = msg;
            // msg potrebbe essere uguale al precedente
            // (dati ricevuti dal device remoto possono essere uguali tra loro).
            mw.oscillator = !mw.oscillator; // questo rende il valore del LiveData diverso dal precedente
            updateMessageData(mw);
        }
    };
    protected abstract void stopCollectingData();


    // communication service to activity
    private MutableLiveData<Integer> live_bt_state;
    public MutableLiveData<Integer> getLive_bt_state() {
        return live_bt_state;
    }
    private MutableLiveData<Integer> live_service_state;
    public MutableLiveData<Integer> getLive_service_state() {
        return live_service_state;
    }
    private MutableLiveData<MessageWrapper> live_message_data;
    public MutableLiveData<MessageWrapper> getLive_message_data() {
        return live_message_data;
    }

    @Override
    public void onCreate() {
        Log.i( TAG, "onCreate");
        super.onCreate();
        live_bt_state = new MutableLiveData<>();
        live_service_state = new MutableLiveData<>();
        live_message_data = new MutableLiveData<>();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i( TAG, "onStartCommand");
        Log.d( TAG, "isStarted:" + isStarted);
        if(!isStarted) {
            isGetBTConnectionsThreadRunning = false;
            bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            updateServiceState(Constants.STARTING_SERVICE);
            if (!bta.isEnabled()) {
                updateBTState(Constants.BT_STATE_DISABLED);
            } else {
                updateBTState(Constants.BT_STATE_ENABLED);
            }
            dw = new DataWrapper();
            mReceiver = new MyBroadcastReceiver(this, null);
            IntentFilter intentFilters = new IntentFilter();
            intentFilters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // bt on/off
            intentFilters.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); // (coach) status changed: discoverable/connectable/none
            intentFilters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // (player) discovery has finished
            registerReceiver(mReceiver, intentFilters);
            return super.onStartCommand(intent, flags, startId);
        }

        updateServiceState(Constants.ALREADY_STARTED_SERVICE);
        return handleRestartCode;

    }

    @Override
    public void onDestroy() {
        Log.i( TAG, "onDestroy");
        if(getBTConnectionsRunnable!=null)
            getBTConnectionsRunnable.cancel();
        if(connectToBTServerRunnable!=null)
            connectToBTServerRunnable.cancel();
        if(connectedDeviceThread!=null)
            connectedDeviceThread.cancel();
        try{unregisterReceiver(mReceiver);}catch (RuntimeException ignored){}
        super.onDestroy();
    }

    @Override
    public void myStop() {
        updateServiceState(Constants.CLOSING_SERVICE);
        if(role==Constants.PLAYER_CHOICE) {
            stopCollectingData();
            try {
                if (bta.isDiscovering())
                    bta.cancelDiscovery();
            }catch (SecurityException ignored){}
            saveFoundDevicesList(null);
        }
        super.myStop();
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    /**
     * Triggered on bt state changed
     * @param state
     */
    @Override
    public void onBluetoothStateChangedEventReceived(int state) {
        switch(state){
            case BluetoothAdapter.STATE_OFF:{
                Log.d(TAG, "BT OFF");
                myStop();
                break;
            }
        }
    }

    /**
     * Triggered on bt scan mode changed
     * @param scanMode
     */
    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {
        switch(scanMode){
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:{
                // device discoverable
                break;
            }
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE: {
                // device non discoverable ma può ricevere connessioni
                if(Integer.valueOf(Constants.BT_STATE_DISCOVERABLE_AND_LISTENING).equals(live_bt_state.getValue())){
                    //assert role == Constants.COACH_CHOICE;
                    // still waiting for client
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
                break;
            }
            case BluetoothAdapter.SCAN_MODE_NONE:{
                // device non discoverbale e non può ricevere connessioni
                if(Integer.valueOf(Constants.BT_STATE_DISCOVERABLE_AND_LISTENING).equals(live_bt_state.getValue())){
                    //assert role == Constants.COACH_CHOICE;
                    // still waiting for client
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
            }
        }
    }

    /**
     * (just for player)
     * Triggered on bt discovery status changed
     * @param isDiscoveryFinished
     */
    @Override
    public void onBluetoothActionDiscoveryEventReceived(boolean isDiscoveryFinished) {
        Log.d(TAG, "i'm discovering:"+!isDiscoveryFinished);
        if(role == Constants.PLAYER_CHOICE) {
            if (Integer.valueOf(Constants.BT_STATE_DISCOVERING).equals(live_bt_state.getValue())) {
                if (isDiscoveryFinished) {
                    // need to start discovering again
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
            }

        }
    }

    public void onDiscoveryStarted(){
        //assert role==Constants.PLAYER_CHOICE;
        updateBTState(Constants.BT_STATE_DISCOVERING);
    }
    public void onDiscoveryStartFail(){
        //assert role==Constants.PLAYER_CHOICE;
        updateBTState(Constants.BT_STATE_ENABLED);
    }

    // unused here
    @Override public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {}

    // utils ---------------------------------------------------------------------------------------
    public void updateBTState(int btState) {
        live_bt_state.setValue(btState);
    }

    private void updateServiceState(int serviceState) {
        live_service_state.setValue(serviceState);
    }

    private void updateMessageData(MessageWrapper msgWrapper){
        live_message_data.setValue(msgWrapper);
    }

    /**
     * Called from bound activity after bt got enabled
     *
     * Qua, potrei non aver abilitato il bluetooth perchè già acceso.
     * Quindi è necessario un update "artificiale" dello stato del bluetooth
     */
    public void onMeEnabled() {
        updateBTState(Constants.BT_STATE_ENABLED);
    }

    // coach ---------------------------------------------------------------------------------------
    /**
     * Called from bound coach activity after bt got discoverable
     *
     * Per semplificare la situazione si rende il dispositivo connectable e discoverable
     * contemporaneamente.
     */
    public void onMeDiscoverable() {
        updateBTState(Constants.BT_STATE_DISCOVERABLE_AND_LISTENING);
        if (isGetBTConnectionsThreadRunning) {
            return;
        }
        isGetBTConnectionsThreadRunning = true;
        // get this listening for incoming connections
        getBTConnectionsRunnable =
                new GetBTConnectionsRunnable(this, bta, this);
        new Thread(getBTConnectionsRunnable).start();
    }
    // OnGetBTConnectionsListener -----
    // (coach callbacks)
    @Override
    public void onNewConnectionEstablished(BluetoothSocket bs) {
        Log.i(TAG, "onNewConnectionEstablished");
        // A connection was accepted. Perform work associated with
        // the connection in a separate thread.
        Log.d(TAG, "new bs: "+bs);
        if(bs!=null){
            connectedDeviceThread = new ConnectedThread(bs, connectedDeviceHandler);
            connectedDeviceThread.start();
        }
        updateBTState(Constants.BT_STATE_CONNECTED);
    }

    @Override
    public void onBTError(String msg) {
        Log.w(TAG, "onBTError msg: "+ msg);
        updateBTState(Constants.BT_STATE_UNSOLVED);
        stopSelf();
    }

    @Override
    public void onPermissionError(String msg) {
        isConnectToBTServerThreadRunning = false;
        // permissions removed during service life
        Log.w(TAG, "onPermissionError msg: " + msg);
        updateBTState(Constants.BT_STATE_BADLY_DENIED);
        stopSelf();
    }

    // player --------------------------------------------------------------------------------------
    private Set<BluetoothDevice> currentFoundDevicesList;

    public void saveFoundDevicesList(Set<BluetoothDevice> list){
        currentFoundDevicesList = list;
    }
    public Set<BluetoothDevice> retriveFoundDeviceList(){
        return currentFoundDevicesList;
    }

    /**
     * Called from bound player activity after user manually selected the device of the coach
     * @param btDevice the device we are gonna connect to
     */
    public void onBTServerSelected(BluetoothDevice btDevice){
        Log.i(TAG, "onBTServerSelected");
        if(isConnectToBTServerThreadRunning)
            return;
        isConnectToBTServerThreadRunning = true;
        // get this listening for incoming connections
        connectToBTServerRunnable =
                new ConnectToBTServerRunnable(this, btDevice, bta, this);
        new Thread(connectToBTServerRunnable).start();
    }

    // DataCollectionListener -----
    // (player)
    @Override
    public void onNewDataCollected(double data) {
        // send this data to server if connected
        if(Integer.valueOf(Constants.BT_STATE_CONNECTED).equals(live_bt_state.getValue())) {
            DataWrapper dw = new DataWrapper(data, System.currentTimeMillis());
            connectedDeviceThread.write(dw);
        }
    }

    // OnConnectToBTServerListener -----
    // (player callbacks)
    @Override
    public void onConnectionEstablished(BluetoothSocket bs) {
        Log.i(TAG, "onConnectionEstablished");
        Log.d(TAG, "coach bs: "+bs);
        isConnectToBTServerThreadRunning = false;
        if(bs!=null){
            connectedDeviceThread = new ConnectedThread(bs, connectedDeviceHandler);
            connectedDeviceThread.start();
        }
        if(!startCollectingData(this)){
            // sensor listener register failed
            updateServiceState(Constants.CLOSING_SERVICE);
            return;
        }
        updateBTState(Constants.BT_STATE_CONNECTED);
    }
    protected abstract boolean startCollectingData(DataCollectionListener dataCollectionListener);

    @Override
    public void onConnectionError(@Nullable BluetoothSocket skt) {
        Log.i(TAG, "onConnectionError");
        isConnectToBTServerThreadRunning = false;
        // tried to connect to a socket but failed
        updateBTState(Constants.BT_STATE_CONNECTION_FAILED);
    }
}
