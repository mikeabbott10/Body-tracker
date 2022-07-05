package it.unipi.sam.volleyballmovementtracker.activities.coach.practices;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.BaseActivity;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments.GetConnectionsFragment;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments.PickerFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPracticingBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyAlertDialog;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class PracticingActivity extends BaseActivity implements View.OnClickListener, Observer<Integer>,
                Animation.AnimationListener, DialogInterface.OnClickListener, ActivityResultCallback<ActivityResult> {
    private static final String TAG = "AAAAPracticActivity";
    protected int currentFragment;
    private MyAlertDialog workInProgressDialog;
    private ActivityPracticingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityPracticingBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        binding.currentTrainingIv.setImageDrawable(
                AppCompatResources.getDrawable(this, currentTrainingDrawableId));
        binding.bluetoothState.setImageDrawable(
                AppCompatResources.getDrawable(this, currentBtStateDrawableId));
        setContentView(binding.getRoot());

        // todo: avvertire utente il bt è stato acceso

        binding.whoAmIIv.setOnClickListener(this);
        binding.currentTrainingIv.setOnClickListener(this);
        binding.bluetoothState.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getCurrentFragment().observe(this, this);

        workInProgressDialog = new MyAlertDialog(
                new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.work_in_progress_warning_title))
                    .setMessage(getString(R.string.work_in_progress_warning))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.accept), this)
                    .setNegativeButton(getString(R.string.decline), null)
                    .create()
        );
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
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
            workInProgressDialog.setObj(Constants.BACK_TO_PICKER_FRAGMENT);
            workInProgressDialog.getAd().show();
            return;
        }
        binding.fragmentContainerMain.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        binding = null;
    }

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
                // è click su spunta verde
                // a fine animazione cambia il fragment
                GraphicUtil.slideDown(binding.bluetoothStateLayout, Constants.FROM_PICKER_TO_NEXT_FRAGMENT, this,300);
            }else{
                // da fare (?) : mostra stato bt
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
    public void onChanged(Integer fragment_id) {
        currentFragment = fragment_id;
        Log.d(TAG, "fragment id:"+fragment_id);
        switch(currentFragment){
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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // "Accept" is clicked on workInProgressDialog.
        if ((int) workInProgressDialog.getObj() == Constants.BACK_TO_PICKER_FRAGMENT) {// back to picker fragment
            GraphicUtil.slideDown(binding.bluetoothStateLayout, Constants.BACK_TO_PICKER_FRAGMENT, this, 300);
        }
    }

    // animation end usata come trigger per l'esecuzione di operazioni come il replace di fragment
    @Override
    public void onAnimationEnd(Animation animation) {
        switch((int)((MyTranslateAnimation)animation).getObj()){
            case Constants.FROM_PICKER_TO_NEXT_FRAGMENT:{
                GraphicUtil.slideUpToOrigin(binding.bluetoothStateLayout, -1, null, 300);
                // start GetConnectionsFragment
                transactionToFragment(GetConnectionsFragment.class);
                break;
            }
            case Constants.BACK_TO_PICKER_FRAGMENT:{
                GraphicUtil.slideUpToOrigin(binding.bluetoothStateLayout, -1, null, 300);

                // start PickerFragment
                transactionToFragment(PickerFragment.class);
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

    // handle discoverability request
    @Override
    public void onActivityResult(ActivityResult result) {
        Log.d(TAG, "onActivityResult");
        viewModel.selectMakingMeDiscoverableValue(false);

        if (result.getResultCode() == GetConnectionsFragment.extraDiscoverableDuration) {
            // i'm now discoverable
            Log.d(TAG, "OKKK");


        } else{
            // not discoverable
            new MaterialAlertDialogBuilder(this)
                    .setTitle( getString(R.string.bt_discoverability_not_enabled))
                    .setMessage(getString(R.string.enable_discoverability))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.accept), ((dialogInterface, i) -> {
                        // try to enable discoverability again
                        viewModel.selectMakingMeDiscoverableValue(true);
                    }))
                    .setNegativeButton(getString(R.string.decline), ((dialogInterface, i) -> {
                        finishAffinity();
                    }))
                    .create().show();
        }
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
