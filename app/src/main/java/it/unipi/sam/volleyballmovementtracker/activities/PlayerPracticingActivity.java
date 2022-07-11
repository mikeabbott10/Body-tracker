package it.unipi.sam.volleyballmovementtracker.activities;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.player.PlayerPracticingFragment;
import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyBTBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class PlayerPracticingActivity extends ServiceBTActivity implements OnBroadcastReceiverOnBTReceiveListener {
    private static final String TAG = "AAAPlayerPracAct";
    private MyBTBroadcastReceiver mReceiver;
    private List<BluetoothDevice> currentFoundDevicesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        currentFoundDevicesList = new ArrayList<>();
        viewModel.selectBtDevicesList(currentFoundDevicesList);
        mReceiver = new MyBTBroadcastReceiver(this);

        // init player practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            currentBtStateDrawableId = R.drawable.ic_baseline_play_arrow_24;
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
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        try {
            // Unregister broadcast listener
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
    public void onChanged(Integer bt_state) {
        super.onChanged(bt_state);
        // bt state changed
        switch(bt_state){
            case Constants.BT_STATE_DISABLED:{
                // ask for enabling bluetooth
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
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.bluetoothState.getId()){
            if(currentFragment==Constants.PLAYER_STARTING_FRAGMENT){
                // start PlayerPracticingFragment
                transactionToFragment(this, PlayerPracticingFragment.class);
                // start servizio
                doStartService(Constants.PLAYER_CHOICE);
                doBindService(BluetoothService.class);
            }else{
                // todo v1.1: mostra stato bt
                GraphicUtil.scaleImage(this, view, -1, null);
            }
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface,i);
        if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            transactionToFragment(this, SelectTrainingFragment.class);
        }
        showingDialog = -1;
    }

    // OnBroadcastReceiverOnBTReceiveListener ------------------------------------------------------
    @Override
    public void onBluetoothActionFoundEventReceived(BluetoothDevice btDevice) {
        currentFoundDevicesList.add(btDevice);
        viewModel.selectBtDevicesList(currentFoundDevicesList);
    }

    //unused here
    @Override public void onBluetoothStateChangedEventReceived(int state) {}
    @Override public void onBluetoothScanModeChangedEventReceived(int scanMode) {}

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
}
