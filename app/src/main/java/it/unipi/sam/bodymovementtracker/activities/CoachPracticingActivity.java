package it.unipi.sam.bodymovementtracker.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.activities.base.CommonPracticingActivity;
import it.unipi.sam.bodymovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.bodymovementtracker.activities.fragments.coach.CoachPracticingFragment;
import it.unipi.sam.bodymovementtracker.services.SensorService;
import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.DataWrapper;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.bodymovementtracker.util.db.DataViewModel;
import it.unipi.sam.bodymovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.bodymovementtracker.util.graphic.ParamTextView;

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
            updateBtIconWithCurrentState(bta.isEnabled());
        }else{
            updateBtIcon(binding.bar.bluetoothState, currentBtStateDrawableId,
                    binding.bar.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
        }
        //assert whoAmIDrawableId != ResourcesCompat.ID_NULL;
        binding.bar.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        // view model per ottenere dati da visualizzare in fase di "practicing"
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
                if(showingDialog!=Constants.BT_ENABLING_DIALOG &&
                        !isRequestBluetoothLaunched && mBoundService != null)
                    askForEnablingBtAfterPermissionCheck();
                break;
            }
            case Constants.BT_STATE_ENABLED:{
                // ask for discoverability
                if(showingDialog!=Constants.DISCOVERABILITY_DIALOG &&
                        !isRequestDiscoverabilityLaunched && mBoundService != null)
                    askForDiscoverabilityAfterPermissionCheck();
                break;
            }
            case Constants.BT_STATE_DISCOVERABLE_AND_LISTENING:{
                Log.d(TAG, "discoverable and listening!");
                break;
            }
            case Constants.BT_STATE_JUST_DISCONNECTED:{
                Log.d(TAG, "remote device disconnected!");
                dataViewModel.deleteAll();
                showMyDialog(Constants.CONNECTION_CLOSED_DIALOG);
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
                Log.d(TAG, "remote device connected!");
                shakeMe(400); // phone vibration 200 ms
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

    private void shakeMe(int ms) {
        // Vibrate for 150 milliseconds
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(ms);
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
                if(currentFragment!=Constants.COACH_PRACTICING_FRAGMENT) {
                    // start PlayerPracticingFragment
                    transactionToFragment(this, CoachPracticingFragment.class, true);
                }
                break;
            }
            case Constants.CLOSING_SERVICE:{
                myStopService();
                mBoundService = null;
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
                //Log.d(TAG, "ricevuto: "+ sd + " con sd.sensor="+ sd.getData());
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

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(showingDialog == Constants.CONNECTION_CLOSED_DIALOG) {
            myStopService();
        }else if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            transactionToFragment(this, SelectTrainingFragment.class, false);
        }else if(showingDialog == Constants.DISCOVERABILITY_DIALOG){
            if(i== AlertDialog.BUTTON_POSITIVE) {
                askForEnablingBtAfterPermissionCheck();
                askForDiscoverabilityAfterPermissionCheck();
            }else{
                myStopServiceAndGoBack();
            }
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
            buildAndShowComicInfo(0, Constants.COACH_CHOICE);
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
                    // start servizio
                    doStartService(Constants.COACH_CHOICE); // idempotent
                    doBindService(SensorService.class); // idempotent
                    break;
                }
                case Constants.ACTION_STOP_TRAINING:{
                    // stop servizio
                    myStopService();
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
                    if(currentFragment!=Constants.COACH_PRACTICING_FRAGMENT && mShouldUnbind)
                        transactionToFragment(this, CoachPracticingFragment.class, true);
                    break;
                }
            }
            hideComicInfo();
        }
    }

    protected void getComicInfoFromBtState(){
        super.getComicInfoFromBtState();
        switch(currentBtState){
            case Constants.BT_STATE_DISCOVERABLE_AND_LISTENING:{
                binding.barComic.barInfoDescriptionTv.setText(getString(R.string.bt_discoverable_stop_question));
                ((ParamTextView)binding.barComic.positiveBtn).setObj(Constants.ACTION_STOP_TRAINING);
                ((ParamTextView)binding.barComic.negativeBtn).setObj(Constants.ACTION_GO_TO_PRACTICING_FRAGMENT);
                binding.barComic.buttons.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    @Override
    protected void onServiceAlreadyStarted() {
        //assert mBoundService!=null;
        if(mBoundService.role != Constants.COACH_CHOICE){
            Snackbar.make(binding.getRoot(), "ERROR 04: role inconsistency", 2000).show();
            Log.e(TAG, "ERROR 04: role inconsistency");
            myStopServiceAndGoBack();
        }
    }

    @Override
    protected void handleDeniedBTEnabling(){
        Log.d(TAG, "handleDeniedBTEnabling");
        super.handleDeniedBTEnabling();
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
