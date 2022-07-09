package it.unipi.sam.volleyballmovementtracker.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.coach.GetConnectionsFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.coach.PickerFragment;
import it.unipi.sam.volleyballmovementtracker.activities.util.CommonPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.GetBTConnectionsRunnable;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.OnGetBTConnectionsListener;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyAlphaAnimation;

public class CoachPracticingActivity extends CommonPracticingActivity implements OnGetBTConnectionsListener {
    private static final String TAG = "AAAACoachPracAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // init coach practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            currentBtStateDrawableId = R.drawable.ic_disabled_ok;
        }
        /*if(currentTrainingDrawableId == ResourcesCompat.ID_NULL) {
            currentTrainingDrawableId = R.drawable.block3;
        }*/
        assert whoAmIDrawableId!= ResourcesCompat.ID_NULL;
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        //binding.middleActionIconIv.setImageDrawable( // it's current training icon here
        //        AppCompatResources.getDrawable(this, currentTrainingDrawableId));
        updateBtIcon(binding.bluetoothState, currentBtStateDrawableId,
                binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);

        mReceiver = new MyBroadcastReceiver(this, this);
        registerReceiver(mReceiver, myIntentFilter);

        setContentView(binding.getRoot());
        showMyDialog(showingDialog);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment!=Constants.PICKER_FRAGMENT){
            // are u sure to go back? (bluetooth sta lavorando e magari anche acquisizione di dati)
            // dialog per tornare a PICKER_FRAGMENT
            showMyDialog(Constants.WORK_IN_PROGRESS_DIALOG);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onChanged(Object obj) {
        if(obj instanceof Integer) {
            currentFragment = (int) obj;
            switch (currentFragment) {
                case Constants.PICKER_FRAGMENT:
                    updateBtIcon(binding.bluetoothState, R.drawable.ic_ok,
                            binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                    setInfoText(getInfoTextFromFragment(currentFragment));
                    break;
                case Constants.GET_CONNECTIONS_FRAGMENT:
                    setInfoText(getInfoTextFromFragment(currentFragment));
                    if(currentScanModeStatus == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                        updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                                binding.bluetoothStateOverlay, R.drawable.waiting, true);
                    }else{
                        updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                                binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                    }
                    break;
            }
        }
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.middleActionIconIv.getId()){
            //GraphicUtil.scaleImage(this, view, -1, null);
        }else if(view.getId()==binding.bluetoothState.getId()){
            if(currentFragment==Constants.PICKER_FRAGMENT){
                // click su spunta verde
                // a fine animazione cambia il fragment e chiedi per discoverability
                GraphicUtil.fadeOut(binding.bluetoothStateLayout, Constants.GO_TO_START_CONNECTION_FRAGMENT, this, 300, true);
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
            GraphicUtil.fadeOut(binding.bluetoothStateLayout,
                    Constants.BACK_TO_INIT_FRAGMENT, this, 300, true);
        }else if(showingDialog == Constants.DISCOVERABILITY_DIALOG){
            if(i== AlertDialog.BUTTON_POSITIVE) {
                checkBTPermissionAndEnableBT();
                askForDiscoverability();
            }else
                finishAffinity();
        }
        showingDialog = -1;
    }

    // animation end usata come trigger per l'esecuzione di operazioni come il replace di fragment
    @Override
    public void onAnimationEnd(/*MyAlphaAnimation*/Animation animation) {
        switch((int)((MyAlphaAnimation)animation).getObj()){
            case Constants.GO_TO_START_CONNECTION_FRAGMENT:{
                GraphicUtil.fadeIn(binding.bluetoothStateLayout, -1, null, 300, true);
                // start GetConnectionsFragment
                transactionToFragment(this, GetConnectionsFragment.class);
                askForDiscoverability();
                break;
            }
            case Constants.BACK_TO_INIT_FRAGMENT:{
                GraphicUtil.fadeIn(binding.bluetoothStateLayout, -1, null, 300, true);

                // start PickerFragment
                transactionToFragment(this, PickerFragment.class);

                resetMyBluetoothStatus();
                break;
            }
        }
    }

    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {
        super.onBluetoothScanModeChangedEventReceived(scanMode);
    }

    @Override public void onBluetoothStateChangedEventReceived(Context context, Intent intent) {
        super.onBluetoothStateChangedEventReceived(context,intent);
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        if (state == BluetoothAdapter.STATE_ON) {
            if (currentFragment == Constants.PICKER_FRAGMENT) {
                // change icon
                updateBtIcon(binding.bluetoothState, R.drawable.ic_ok,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
            }
            if (currentFragment == Constants.GET_CONNECTIONS_FRAGMENT) {
                // change icon
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                askForDiscoverability();
            }
        }
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        super.onActivityResult(result);

        // handle discoverability request
        if (result.getResultCode() != Activity.RESULT_CANCELED) {
            // i'm now discoverable
            Log.d(TAG, "I'm discoverable");
            // update icon
            updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                    binding.bluetoothStateOverlay, R.drawable.waiting, true);
            new Thread(
                    new GetBTConnectionsRunnable(bta, this)
            ).start();
        } else{
            // not discoverable
            showMyDialog(Constants.DISCOVERABILITY_DIALOG);
        }
    }

    @Override
    public void onNewConnectionEstablished(BluetoothSocket bs) {
        Log.d(TAG, "new bs: "+bs.toString());
        Snackbar.make(binding.getRoot(), "new bs: "+bs.toString(), 5000).show();
    }

    // utils--------------------------------------------------------
    protected Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.PICKER_FRAGMENT:
                return PickerFragment.class;
            case Constants.GET_CONNECTIONS_FRAGMENT:
                return GetConnectionsFragment.class;
        }
        return PickerFragment.class;
    }

    protected String getInfoTextFromFragment(int currentFragment) {
        switch(currentFragment){
            // coach fragments
            case Constants.PICKER_FRAGMENT:
                return getString(R.string.picker_fragment_info);
            case Constants.GET_CONNECTIONS_FRAGMENT:
                return getString(R.string.get_connections_fragment_info);
        }
        return null;
    }
}
