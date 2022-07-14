package it.unipi.sam.volleyballmovementtracker.services;

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

import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MessageWrapper;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.ConnectToBTServerRunnable;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.ConnectedThread;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.GetBTConnectionsRunnable;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnConnectToBTServerListener;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnGetBTConnectionsListener;

public abstract class BluetoothService extends NotificationService implements OnGetBTConnectionsListener,
        OnConnectToBTServerListener, OnBroadcastReceiverOnBTReceiveListener {
    private static String TAG = "SSSSBluetoothService";
    protected BluetoothAdapter bta;
    protected BroadcastReceiver mReceiver;
    protected GetBTConnectionsRunnable getBTConnectionsRunnable;
    protected ConnectToBTServerRunnable connectToBTServerRunnable;
    public int role;

    public ConnectedThread connectedDeviceThread;
    private final Handler connectedDeviceHandler = new Handler(Looper.getMainLooper()) {
        boolean oscillator = true;
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == Constants.STREAM_DISCONNECTED){
                updateBTState(Constants.BT_STATE_JUST_DISCONNECTED);
                connectedDeviceThread.cancel();
                return;
            }
            // msg potrebbe essere uguale al precedente
            // (dati ricevuti dal device remoto possono essere uguali tra loro).
            oscillator = !oscillator; // questo rende il valore del LiveData diverso dal precedente
            updateMessageData(new MessageWrapper(msg, oscillator));
        }
    };


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
            intentFilters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // bt on/off
            intentFilters.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); // (coach) status changed: discoverable/connectable/none
            intentFilters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // (player) discovery has finished
            intentFilters.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); // (player) discovery has started
            registerReceiver(mReceiver, intentFilters);
            return super.onStartCommand(intent, flags, startId);
        }else {
            updateServiceState(Constants.ALREADY_STARTED_SERVICE);
            return handleRestartCode;
        }
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
                    assert role == Constants.COACH_CHOICE;
                    // still waiting for client
                    updateBTState(Constants.BT_STATE_ENABLED);
                }
                break;
            }
            case BluetoothAdapter.SCAN_MODE_NONE:{
                // device non discoverbale e non può ricevere connessioni
                if(Integer.valueOf(Constants.BT_STATE_DISCOVERABLE_AND_LISTENING).equals(live_bt_state.getValue())){
                    assert role == Constants.COACH_CHOICE;
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
            } else {
                if (!isDiscoveryFinished) {
                    // just started discovering
                    updateBTState(Constants.BT_STATE_DISCOVERING);
                }
            }

        }
    }

    // unused here
    @Override public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {}

    // utils ---------------------------------------------------------------------------------------
    protected void updateBTState(int btState) {
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
     */
    public void onMeEnabled() {
        updateBTState(Constants.BT_STATE_ENABLED);
    }

    // coach ---------------------------------------------------------------------------------------
    /**
     * Called from bound coach activity after bt got discoverable
     */
    public void onMeDiscoverable() {
        updateBTState(Constants.BT_STATE_DISCOVERABLE_AND_LISTENING);
        // get this listening for incoming connections
        getBTConnectionsRunnable =
                new GetBTConnectionsRunnable(this, bta, this);
        new Thread(getBTConnectionsRunnable).start();
    }
    // OnGetBTConnectionsListener -----
    // (coach callbacks)
    @Override
    public void onNewConnectionEstablished(BluetoothSocket bs) {
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
        Log.e(TAG, "onBTError msg: "+ msg);
        updateBTState(Constants.BT_STATE_UNSOLVED);
        stopSelf();
    }

    @Override
    public void onPermissionError(String obj) {
        // permissions removed during service life
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
        // get this listening for incoming connections
        connectToBTServerRunnable =
                new ConnectToBTServerRunnable(this, btDevice, bta, this);
        new Thread(connectToBTServerRunnable).start();
    }

    // OnConnectToBTServerListener -----
    // (player callbacks)
    @Override
    public void onConnectionEstablished(BluetoothSocket bs) {
        Log.d(TAG, "coach bs: "+bs);
        if(bs!=null){
            connectedDeviceThread = new ConnectedThread(bs, connectedDeviceHandler);
            connectedDeviceThread.start();
        }
        updateBTState(Constants.BT_STATE_CONNECTED);
        startCollectingData();
    }
    protected abstract void startCollectingData();

    @Override
    public void onConnectionError(@Nullable BluetoothSocket skt) {
        // tried to connect to a socket but failed
        updateBTState(Constants.BT_STATE_CONNECTION_FAILED);
    }
}
