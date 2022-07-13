package it.unipi.sam.volleyballmovementtracker.activities;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.base.CommonPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.player.PlayerPracticingFragment;
import it.unipi.sam.volleyballmovementtracker.services.player.PlayerService;
import it.unipi.sam.volleyballmovementtracker.util.BtAndServiceStatesWrapper;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class PlayerPracticingActivity extends CommonPracticingActivity {
    private static final String TAG = "AAAPlayerPracAct";
    private List<BluetoothDevice> currentFoundDevicesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        currentFoundDevicesList = new ArrayList<>();
        viewModel.selectBtDevicesList(currentFoundDevicesList);

        // init player practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            currentBtStateDrawableId = bta.isEnabled()?R.drawable.ic_bluetooth1:R.drawable.disabled_bt;
        }
        assert whoAmIDrawableId != ResourcesCompat.ID_NULL;
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        updateBtIcon(binding.bluetoothState, currentBtStateDrawableId,
                binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);

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

    @Override
    public void onChanged(BtAndServiceStatesWrapper btAndServiceStatesWrapper) {
        super.onChanged(btAndServiceStatesWrapper);
        if(btAndServiceStatesWrapper.getServiceState()==-1){
            // bt state changed
            int bt_state = btAndServiceStatesWrapper.getBtState();
            switch(bt_state){
                case Constants.BT_STATE_DISABLED:{
                    // ask for enabling bluetooth
                    if(showingDialog!=Constants.BT_ENABLING_DIALOG && !isRequestBluetoothLaunched)
                        askForEnablingBt();
                    break;
                }
                case Constants.BT_STATE_ENABLED:{
                    break;
                }
                case Constants.BT_STATE_CONNECTING:{
                    break;
                }
                case Constants.BT_STATE_CONNECTED:{
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
            return;
        }
        if(btAndServiceStatesWrapper.getBtState()==-1){
            // service state changed
            int serviceState = btAndServiceStatesWrapper.getServiceState();
            switch (serviceState){
                case Constants.STARTING_SERVICE:{
                    break;
                }
                case Constants.CLOSING_SERVICE:{
                    myStopService();
                    transactionToFragment(this,
                            SelectTrainingFragment.class, false);
                    updateBtIconWithCurrentState(bta.isEnabled());
                    break;
                }
            }
        }
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.bluetoothState.getId()){
            if(currentFragment==Constants.PLAYER_STARTING_FRAGMENT){
                // start PlayerPracticingFragment
                transactionToFragment(this, PlayerPracticingFragment.class, true);
                // start servizio
                doStartService(Constants.PLAYER_CHOICE); // idempotent
                doBindService(PlayerService.class); // idempotent
            }else{
                // todo v1.1: mostra stato bt
                GraphicUtil.scaleImage(this, view, -1, null);
            }
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            transactionToFragment(this, SelectTrainingFragment.class, false);
        }
        super.onClick(dialogInterface,i);
    }

    @Override
    protected void onServiceAlreadyStarted() {
        assert mBoundService!=null;
        if(mBoundService.role != Constants.PLAYER_CHOICE){
            Snackbar.make(binding.getRoot(), "ERROR 04: role inconsistency", 2000).show();
            Log.e(TAG, "ERROR 04: role inconsistency");
            myStopService();
            return;
        }
        // retrive so far found devices
        try{
            currentFoundDevicesList = ((PlayerService)mBoundService).retriveFoundDeviceList();
            viewModel.selectBtDevicesList(currentFoundDevicesList);
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
    protected void myStopService() {
        super.myStopService();
        transactionToFragment(this, SelectTrainingFragment.class, false);
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    @Override
    public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {
        try{
            currentFoundDevicesList.add(btDevice);
            viewModel.selectBtDevicesList(currentFoundDevicesList);
            ((PlayerService)mBoundService).saveFoundDevicesList(currentFoundDevicesList);
        }catch(Exception e){
            Log.e(TAG, "", e);
        }
    }

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
