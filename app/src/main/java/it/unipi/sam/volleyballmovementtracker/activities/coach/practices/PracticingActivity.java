package it.unipi.sam.volleyballmovementtracker.activities.coach.practices;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments.GetConnectionsFragment;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments.PickerFragment;
import it.unipi.sam.volleyballmovementtracker.activities.util.BaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPracticingBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.GetBTConnectionsRunnable;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.OnGetBTConnectionsListener;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class PracticingActivity extends BaseActivity implements View.OnClickListener, Observer<Object>,
        Animation.AnimationListener, OnGetBTConnectionsListener {
    private static final String TAG = "AAAAPracticActivity";
    protected int currentFragment;
    private ActivityPracticingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        showingDialog = -1;
        // get custom instance state or original instance state
        if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
        }else {
            super.onCreate(null);
        }

        binding = ActivityPracticingBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        binding.currentTrainingIv.setImageDrawable(
                AppCompatResources.getDrawable(this, currentTrainingDrawableId));
        binding.bluetoothState.setImageDrawable(
                AppCompatResources.getDrawable(this, currentBtStateDrawableId));
        setContentView(binding.getRoot());

        binding.whoAmIIv.setOnClickListener(this);
        binding.currentTrainingIv.setOnClickListener(this);
        binding.bluetoothState.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getCurrentFragment().observe(this, this);

        initDialog();
        showMyDialog(showingDialog);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        binding.fragmentContainerMain.setVisibility(View.VISIBLE);
        // While your activity is in the STARTED lifecycle state
        // or higher, fragments can be added, replaced, or removed
        transactionToFragment(getFragmentClassFromModel());
    }

    @Override
    public void onBackPressed() {
        if(currentFragment!=Constants.PICKER_FRAGMENT){
            // are u sure to go back? (bluetooth sta lavorando e magari anche acquisizione di dati)
            // dialog per tornare a picker fragment
            showMyDialog(Constants.WORK_IN_PROGRESS_DIALOG);
            return;
        }
        binding.fragmentContainerMain.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        binding = null;
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        if(view.getId()==binding.backBtn.getId()){
            onBackPressed();
        }else if(view.getId()==binding.whoAmIIv.getId()){
            GraphicUtil.scaleImage(this, view, -1, null);
        }else if(view.getId()==binding.currentTrainingIv.getId()){
            GraphicUtil.scaleImage(this, view, -1, null);
        }else if(view.getId()==binding.bluetoothState.getId()){
            if(currentFragment==Constants.PICKER_FRAGMENT){
                // click su spunta verde
                // a fine animazione cambia il fragment e chiedi per discoverability
                GraphicUtil.slideDown(binding.bluetoothStateLayout, Constants.FROM_PICKER_TO_NEXT_FRAGMENT, this,300);
            }else{
                // todo v1.1: mostra stato bt
                GraphicUtil.scaleImage(this, view, -1, null);
            }
        }
    }

    private void transactionToFragment(Class<? extends Fragment> clas) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container_main, clas, null)
            .setReorderingAllowed(true)
            .commit();
    }

    @Override
    public void onChanged(Object obj) {
        if(obj instanceof Integer) {
            currentFragment = (int) obj;
            switch (currentFragment) {
                case Constants.PICKER_FRAGMENT:
                    binding.bluetoothState.setImageDrawable(
                            AppCompatResources.getDrawable(this, R.drawable.ic_ok));
                    binding.bluetoothStateOverlay.setImageDrawable(null);
                    break;
                case Constants.GET_CONNECTIONS_FRAGMENT:
                    binding.bluetoothState.setImageDrawable(
                            AppCompatResources.getDrawable(this, R.drawable.ic_bluetooth1));
                    break;
            }
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface,i);
        if(showingDialog == Constants.DISCOVERABILITY_DIALOG){
            if(i==AlertDialog.BUTTON_POSITIVE)
                askForDiscoverability();
            else
                finishAffinity();
        }else if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            GraphicUtil.slideDown(binding.bluetoothStateLayout, Constants.BACK_TO_PICKER_FRAGMENT, this, 300);
        }else if(showingDialog== Constants.BT_ENABLING_DIALOG){
            if(i==AlertDialog.BUTTON_POSITIVE) {
                checkBTPermissionAndEnableBT();
            }else
                finishAffinity();
        }
        showingDialog = -1;
    }

    // animation end usata come trigger per l'esecuzione di operazioni come il replace di fragment
    @Override
    public void onAnimationEnd(Animation animation) {
        switch((int)((MyTranslateAnimation)animation).getObj()){
            case Constants.FROM_PICKER_TO_NEXT_FRAGMENT:{
                GraphicUtil.slideUpToOrigin(binding.bluetoothStateLayout, -1, null, 300);
                // start GetConnectionsFragment
                transactionToFragment(GetConnectionsFragment.class);
                askForDiscoverability();
                break;
            }
            case Constants.BACK_TO_PICKER_FRAGMENT:{
                GraphicUtil.slideUpToOrigin(binding.bluetoothStateLayout, -1, null, 300);

                // start PickerFragment
                transactionToFragment(PickerFragment.class);

                resetMyBluetoothStatus();
                break;
            }
        }
    }

    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {
        super.onBluetoothScanModeChangedEventReceived(scanMode);
    }

    @Override public void onBluetoothStateChangedEventReceived(Context context, Intent intent) {
        super.onBluetoothStateChangedEventReceived(context,intent);
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                // bluetooth on
                if(currentFragment==Constants.GET_CONNECTIONS_FRAGMENT){
                    askForDiscoverability();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        super.onActivityResult(result);

        // handle discoverability request
        if (result.getResultCode() != Activity.RESULT_CANCELED) {
            // i'm now discoverable
            Log.d(TAG, "I'm discoverable");
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
    }

    // utils--------------------------------------------------------
    private Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.PICKER_FRAGMENT:
                return PickerFragment.class;
            case Constants.GET_CONNECTIONS_FRAGMENT:
                return GetConnectionsFragment.class;
        }
        return PickerFragment.class;
    }
}
