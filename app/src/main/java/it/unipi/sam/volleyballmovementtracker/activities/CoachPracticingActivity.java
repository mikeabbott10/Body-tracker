package it.unipi.sam.volleyballmovementtracker.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.base.CommonPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.coach.CoachPracticingFragment;
import it.unipi.sam.volleyballmovementtracker.services.SensorService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.db.DataViewModel;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class CoachPracticingActivity extends CommonPracticingActivity
        implements OnBroadcastReceiverOnBTReceiveListener {
    private static final String TAG = "AAAACoachPracAct";
    private DataViewModel dataViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // init coach practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            currentBtStateDrawableId = bta.isEnabled()?R.drawable.ic_bluetooth1:R.drawable.disabled_bt;
        }
        assert whoAmIDrawableId != ResourcesCompat.ID_NULL;
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        updateBtIcon(binding.bluetoothState, currentBtStateDrawableId,
                binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

        setContentView(binding.getRoot());
        showMyDialog(showingDialog);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        registerReceiver(mReceiver, myIntentFilter);
    }

    @Override
    public void onPause() {
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
                // ask for discoverability
                if(showingDialog!=Constants.DISCOVERABILITY_DIALOG && !isRequestDiscoverabilityLaunched)
                    askForDiscoverabilityAfterPermissionCheck();
                break;
            }
            case Constants.BT_STATE_DISCOVERABLE_AND_LISTENING:{
                Log.d(TAG, "discoverable and listening!");
                break;
            }
            case Constants.BT_STATE_JUST_DISCONNECTED:{
                Log.d(TAG, "remote device disconnected!");
                showMyDialog(Constants.CONNECTION_CLOSED_DIALOG);
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
                Log.d(TAG, "remote device connected!");

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
                assert mBoundService!=null;
                if(currentFragment!=Constants.COACH_PRACTICING_FRAGMENT) {
                    // start PlayerPracticingFragment
                    transactionToFragment(this, CoachPracticingFragment.class, true);
                }
                break;
            }
            case Constants.CLOSING_SERVICE:{
                myStopService();
                //getSupportFragmentManager().popBackStack();
                //transactionToFragment(this,
                //        SelectTrainingFragment.class, false);
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
                // message received
                DataWrapper sd = (DataWrapper) msg.obj;
                if(sd==null)
                    return;
                dataViewModel.insert(sd);
                Log.d(TAG, "ricevuto: "+ sd + " con sd.sensor="+ sd.getData());
                break;
            }
            case Constants.MESSAGE_WRITE:{
                // idk what to do here
                break;
            }
            case Constants.MESSAGE_TOAST:{
                Snackbar.make(binding.getRoot(), msg.getData().getString("toast"), 2000).show();
                break;
            }
        }
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.bluetoothState.getId()){
            if(currentFragment==Constants.COACH_STARTING_FRAGMENT){
                // start servizio
                doStartService(Constants.COACH_CHOICE); // idempotent
                doBindService(SensorService.class); // idempotent
            }else{
                // todo v1.1: mostra stato bt
                GraphicUtil.scaleImage(this, view, -1, null);
            }
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(showingDialog == Constants.CONNECTION_CLOSED_DIALOG) {
            if(currentFragment == Constants.COACH_PRACTICING_FRAGMENT){
                // go back to prev fragment
                myStopService();
                transactionToFragment(this, SelectTrainingFragment.class, false);
            }
        }else if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            transactionToFragment(this, SelectTrainingFragment.class, false);
        }else if(showingDialog == Constants.DISCOVERABILITY_DIALOG){
            if(i== AlertDialog.BUTTON_POSITIVE) {
                askForEnablingBtAfterPermissionCheck();
                askForDiscoverabilityAfterPermissionCheck();
            }else{
                myStopService();
            }
        }
        super.onClick(dialogInterface,i);
    }

    @Override
    protected void onServiceAlreadyStarted() {
        assert mBoundService!=null;
        if(mBoundService.role != Constants.COACH_CHOICE){
            Snackbar.make(binding.getRoot(), "ERROR 04: role inconsistency", 2000).show();
            Log.e(TAG, "ERROR 04: role inconsistency");
            myStopService();
        }
    }

    @Override
    protected void handleDeniedBTEnabling(){
        Log.d(TAG, "handleDeniedBTEnabling");
        super.handleDeniedBTEnabling();
        transactionToFragment(this, SelectTrainingFragment.class, false);
    }

    @Override
    protected void myStopService() {
        super.myStopService();
        transactionToFragment(this, SelectTrainingFragment.class, false);
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        // handle discoverability request
        if (result.getResultCode() != Activity.RESULT_CANCELED) {
            // i'm now discoverable
            if(mBoundService!=null)
                mBoundService.onMeDiscoverable();
        } else{
            // not discoverable
            showMyDialog(Constants.DISCOVERABILITY_DIALOG);
        }
        isRequestDiscoverabilityLaunched = false;
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    // unused here
    @Override public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {}
    @Override public void onBluetoothActionDiscoveryEventReceived(boolean isDiscoveryFinished) {}

    // utils--------------------------------------------------------
    protected Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.COACH_STARTING_FRAGMENT:
                return SelectTrainingFragment.class;
            case Constants.COACH_PRACTICING_FRAGMENT:
                return CoachPracticingFragment.class;
        }
        return SelectTrainingFragment.class;
    }

    protected String getInfoTextFromFragment(int currentFragment) {
        switch(currentFragment){
            // coach fragments
            case Constants.COACH_STARTING_FRAGMENT:
                return getString(R.string.starting_coach_info);
            case Constants.COACH_PRACTICING_FRAGMENT:
                return getString(R.string.practicing_coach_info);
        }
        return null;
    }
}
