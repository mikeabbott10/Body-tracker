package it.unipi.sam.volleyballmovementtracker.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.coach.CoachPracticingFragment;
import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class CoachPracticingActivity extends ServiceBTActivity{
    private static final String TAG = "AAAACoachPracAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // init coach practicing activity bar
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
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
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
                // ask for discoverability
                askForDiscoverability();
                break;
            }
            case Constants.BT_STATE_DISCOVERABLE:{
                break;
            }
            case Constants.BT_STATE_LISTEN:{
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
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
            if(currentFragment==Constants.COACH_STARTING_FRAGMENT){
                // start CoachPracticingFragment
                transactionToFragment(this, CoachPracticingFragment.class);
                // start servizio
                doStartService(Constants.COACH_CHOICE);
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
        }else if(showingDialog == Constants.DISCOVERABILITY_DIALOG){
            if(i== AlertDialog.BUTTON_POSITIVE) {
                askForEnablingBt();
                askForDiscoverability();
            }else
                finishAffinity();
        }
        showingDialog = -1;
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        super.onActivityResult(result);
        // handle discoverability request
        if (result.getResultCode() != Activity.RESULT_CANCELED) {
            // i'm now discoverable
            Log.d(TAG, "I'm discoverable");
            if(mBoundService!=null)
                mBoundService.onMeDiscoverable();
        } else{
            // not discoverable
            showMyDialog(Constants.DISCOVERABILITY_DIALOG);
        }
    }

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
