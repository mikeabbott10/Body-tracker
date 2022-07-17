package it.unipi.sam.bodymovementtracker.activities;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.Set;

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.activities.base.CommonPracticingActivity;
import it.unipi.sam.bodymovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.bodymovementtracker.activities.fragments.player.PlayerPracticingFragment;
import it.unipi.sam.bodymovementtracker.services.SensorService;
import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnFoundDeviceSelectedListener;
import it.unipi.sam.bodymovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.bodymovementtracker.util.graphic.ParamTextView;

public class PlayerPracticingActivity extends CommonPracticingActivity implements OnFoundDeviceSelectedListener{
    private static final String TAG = "AAAPlayerPracAct";
    public MutableLiveData<Set<BluetoothDevice>> currentFoundDevicesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        currentFoundDevicesList = new MutableLiveData<>();
        currentFoundDevicesList.setValue(new HashSet<>());

        // init player practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            updateBtIconWithCurrentState(bta.isEnabled());
        }else{
            updateBtIcon(binding.bar.bluetoothState, currentBtStateDrawableId,
                    binding.bar.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
        }
        //assert whoAmIDrawableId != ResourcesCompat.ID_NULL;
        binding.bar.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        setContentView(binding.getRoot());
        showMyDialog(showingDialog);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        myIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, myIntentFilter);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        try {
            // Unregister broadcast receiver
            unregisterReceiver(mReceiver);
        } catch(IllegalArgumentException e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * Triggered on service BT state change
     * @param bt_state
     */
    protected void handleServiceBTStateChange(int bt_state) {
        super.handleServiceBTStateChange(bt_state);
        switch(bt_state){
            case Constants.BT_STATE_DISABLED:{
                // ask for enabling bluetooth
                if(showingDialog!=Constants.BT_ENABLING_DIALOG && !isRequestBluetoothLaunched)
                    askForEnablingBtAfterPermissionCheck();
                break;
            }
            case Constants.BT_STATE_ENABLED:{
                // ask for enabling bluetooth
                if(showingDialog!=Constants.BT_START_DISCOVERY_PERMISSIONS_DIALOG) {
                    //assert mBoundService!=null;
                    if (startDiscoveryAfterPermissionCheck()) {
                        // start discovery started
                        mBoundService.onDiscoveryStarted();
                    } else {
                        mBoundService.onDiscoveryStartFail();
                    }
                }
                break;
            }
            case Constants.BT_STATE_DISCOVERING:{
                Log.d(TAG, "discovering!");
                break;
            }
            case Constants.BT_STATE_JUST_DISCONNECTED:{
                Log.d(TAG, "remote device disconnected!");
                showMyDialog(Constants.CONNECTION_CLOSED_DIALOG);
                break;
            }
            case Constants.BT_STATE_CONNECTION_FAILED:{
                Log.d(TAG, "remote connection failed!");
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
                Log.d(TAG, "remote device connected!");
                break;
            }
            case Constants.BT_STATE_PERMISSION_REQUIRED:{
                break;
            }
            case Constants.BT_STATE_BADLY_DENIED:{
                break;
            }
            case Constants.BT_STATE_UNSOLVED: {
                break;
            }
        }
    }

    /**
     * Triggered on service state change
     * @param serviceState
     */
    protected void handleServiceStateChange(int serviceState) {
        super.handleServiceStateChange(serviceState);
        switch (serviceState){
            case Constants.STARTING_SERVICE:
            case Constants.ALREADY_STARTED_SERVICE:{
                //assert mBoundService!=null;
                if(currentFragment!=Constants.PLAYER_PRACTICING_FRAGMENT) {
                    // start PlayerPracticingFragment
                    transactionToFragment(this, PlayerPracticingFragment.class, true);
                }
                break;
            }
            case Constants.CLOSING_SERVICE:{
                myStopServiceAndGoBack();
                updateBtIconWithCurrentState(bta.isEnabled());
                break;
            }
        }
    }

    /**
     * Triggered on new message from service
     * @param msg
     */
    protected void handleReceivedDataFromMessage(Message msg) {
        switch(msg.what){
            case Constants.MESSAGE_READ:{
                // show received message
                break;
            }
            case Constants.MESSAGE_WRITE:{
                // nothing to do here
                break;
            }
            case Constants.MESSAGE_TOAST:{
                Snackbar.make(binding.getRoot(), msg.getData().getString("toast"), 2000).show();
                break;
            }
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(showingDialog == Constants.CONNECTION_CLOSED_DIALOG) {
            myStopService();
        }else if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            transactionToFragment(this, SelectTrainingFragment.class, false);
        }
        super.onClick(dialogInterface,i);
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.bar.whoAmIIv.getId()){
            if(isComicShown()){
                hideComicInfo();
                return;
            }
            GraphicUtil.scaleImage(this, view, -1, null);
            buildAndShowComicInfo(0, Constants.PLAYER_CHOICE);
            return;
        }
        if(view.getId()==binding.bar.bluetoothState.getId()){
            if(isComicShown()){
                hideComicInfo();
                return;
            }
            GraphicUtil.scaleImage(this, view, -1, null);
            buildAndShowComicInfo(2, -1);
            return;
        }
        if(view.getId()==binding.barComic.positiveBtn.getId()) {
            int action;
            try {
                action = (int)((ParamTextView)binding.barComic.positiveBtn).getObj();
            } catch (Exception e){
                Log.w(TAG, "", e);
                hideComicInfo();
                return;
            }
            switch(action){
                case Constants.ACTION_START_TRAINING:{
                    // init found devices list
                    currentFoundDevicesList = new MutableLiveData<>();
                    currentFoundDevicesList.setValue(new HashSet<>());
                    // start servizio
                    doStartService(Constants.PLAYER_CHOICE); // idempotent
                    doBindService(SensorService.class); // idempotent
                    break;
                }
                case Constants.ACTION_STOP_TRAINING:{
                    // stop servizio
                    myStopService();
                    break;
                }
                case Constants.ACTION_START_DISCOVERING:{
                    // start discovery again updating service bt state
                    //assert mBoundService!=null;
                    mBoundService.updateBTState(Constants.NULL_STATE);
                    mBoundService.updateBTState(bta.isEnabled() ?
                            Constants.BT_STATE_ENABLED : Constants.BT_STATE_DISABLED);
                    break;
                }
            }
            hideComicInfo();
        }else if(view.getId()==binding.barComic.negativeBtn.getId()) {
            int action;
            try {
                action = (int)((ParamTextView)binding.barComic.negativeBtn).getObj();
            } catch (Exception e){
                Log.w(TAG, "", e);
                hideComicInfo();
                return;
            }
            switch(action){
                case Constants.ACTION_STOP_TRAINING:{
                    // stop servizio
                    myStopService();
                    break;
                }
                case Constants.ACTION_GO_TO_PRACTICING_FRAGMENT:{
                    if(currentFragment!=Constants.PLAYER_PRACTICING_FRAGMENT && mShouldUnbind)
                        transactionToFragment(this, PlayerPracticingFragment.class, true);
                    break;
                }
            }
            hideComicInfo();
        }
    }

    protected void getComicInfoFromBtState(){
        super.getComicInfoFromBtState();
        switch(currentBtState){
            case Constants.BT_STATE_DISCOVERING:{
                binding.barComic.barInfoDescriptionTv.setText(getString(R.string.bt_discovering_stop_question));
                ((ParamTextView)binding.barComic.positiveBtn).setObj(Constants.ACTION_STOP_TRAINING);
                ((ParamTextView)binding.barComic.negativeBtn).setObj(Constants.ACTION_GO_TO_PRACTICING_FRAGMENT);
                binding.barComic.buttons.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.BT_STATE_CONNECTION_FAILED:{
                binding.barComic.barInfoDescriptionTv.setText(getString(R.string.bt_connection_failed_discovery_question));
                ((ParamTextView)binding.barComic.positiveBtn).setObj(Constants.ACTION_START_DISCOVERING);
                ((ParamTextView)binding.barComic.negativeBtn).setObj(Constants.ACTION_STOP_TRAINING);
                binding.barComic.buttons.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    protected void myStopService() {
        currentFoundDevicesList.removeObservers(this);
        currentFoundDevicesList = null;
        super.myStopService();
    }

    @Override
    protected void onServiceAlreadyStarted() {
        //assert mBoundService!=null;
        if(mBoundService.role != Constants.PLAYER_CHOICE){
            Snackbar.make(binding.getRoot(), "ERROR 04: role inconsistency", 2000).show();
            Log.e(TAG, "ERROR 04: role inconsistency");
            myStopServiceAndGoBack();
            return;
        }
        // retrive so far found devices
        try{
            if(mBoundService.retriveFoundDeviceList()!=null)
                currentFoundDevicesList.setValue(mBoundService.retriveFoundDeviceList());
        }catch(Exception e){
            Log.e(TAG, "", e);
        }
    }

    @Override
    protected void handleDeniedBTEnabling(){
        Log.d(TAG, "handleDeniedBTEnabling");
        super.handleDeniedBTEnabling();
        transactionToFragment(this, SelectTrainingFragment.class, false);
    }

    @Override
    public void onDeviceSelected(BluetoothDevice btd) {
        if(mBoundService!=null)
            mBoundService.onBTServerSelected(btd);
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    @Override
    public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {
        try{
            Set<BluetoothDevice> btDevicesSet = currentFoundDevicesList.getValue();
            if (btDevicesSet != null) {
                btDevicesSet.add(btDevice);
            }
            currentFoundDevicesList.setValue(btDevicesSet);
            mBoundService.saveFoundDevicesList(btDevicesSet);
        }catch(Exception e){
            Log.e(TAG, "", e);
        }
    }

    // unused here
    @Override public void onBluetoothActionDiscoveryEventReceived(boolean isDiscoveryFinished) {}

    // utils--------------------------------------------------------
    protected Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.PLAYER_STARTING_FRAGMENT:
                return SelectTrainingFragment.class;
            case Constants.PLAYER_PRACTICING_FRAGMENT:
                return PlayerPracticingFragment.class;
        }
        return SelectTrainingFragment.class;
    }

    protected String getInfoTextFromFragment(int currentFragment) {
        switch(currentFragment){
            // coach fragments
            case Constants.PLAYER_STARTING_FRAGMENT:
                return getString(R.string.starting_player_info);
            case Constants.PLAYER_PRACTICING_FRAGMENT:
                return getString(R.string.practicing_player_info);
        }
        return null;
    }

    // unused here
    @Override public void onActivityResult(ActivityResult result) {}
}
