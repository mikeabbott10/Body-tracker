package it.unipi.sam.volleyballmovementtracker.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.player.PlayerPracticingFragment;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.player.StartingPlayerFragment;
import it.unipi.sam.volleyballmovementtracker.activities.util.CommonPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyAlphaAnimation;

public class PlayerPracticingActivity extends CommonPracticingActivity {
    private static final String TAG = "AAAPlayerPracAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init player practicing activity bar
        if(currentBtStateDrawableId == ResourcesCompat.ID_NULL) {
            currentBtStateDrawableId = R.drawable.ic_bluetooth1;
            currentVideoPlayerId = R.drawable.ic_baseline_video_library_24;
        }
        assert whoAmIDrawableId!=ResourcesCompat.ID_NULL;
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        /*binding.middleActionIconIv.setImageDrawable( // it's video playlist icon here
                AppCompatResources.getDrawable(this, currentVideoPlayerId));*/
        binding.bluetoothState.setImageDrawable(
                AppCompatResources.getDrawable(this, currentBtStateDrawableId));

        setContentView(binding.getRoot());
        showMyDialog(showingDialog);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment==Constants.PLAYER_PRACTICING_FRAGMENT){
            // are u sure to go back? (bluetooth sta lavorando e magari anche acquisizione di dati)
            // dialog per tornare a picker fragment
            showMyDialog(Constants.WORK_IN_PROGRESS_DIALOG);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onChanged(Object obj) {
        if(obj instanceof Integer) {
            currentFragment = (int) obj;
            /*switch (currentFragment) {
                case Constants.PLAYER_STARTING_FRAGMENT:
                    break;
                case Constants.PLAYER_PRACTICING_FRAGMENT:
                    break;
                case Constants.SELECT_TRAINING_FRAGMENT:
                    break;
            }
            binding.middleActionIconIv.setImageDrawable( // it's video playlist icon here
                    AppCompatResources.getDrawable(this, currentVideoPlayerId));*/
            setInfoText(getInfoTextFromFragment(currentFragment));
        }
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.middleActionIconIv.getId()){
            /*GraphicUtil.fadeOut(binding.middleActionIconIv,
                    Constants.SELECT_TRAINING_FRAGMENT, this,70, false);*/
        }else if(view.getId()==binding.bluetoothState.getId()){
            GraphicUtil.scaleImage(this, view, -1, null);
        }
    }

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface,i);
        if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // "Accept" is clicked on workInProgressDialog.
            GraphicUtil.fadeOut(binding.bluetoothStateLayout,
                    Constants.BACK_TO_INIT_FRAGMENT, this, 70, true);
        }
        showingDialog = -1;
    }

    // animation end usata come trigger per l'esecuzione di operazioni come il replace di fragment
    @Override
    public void onAnimationEnd(/*MyAlphaAnimation*/Animation animation) {
        switch((int)((MyAlphaAnimation)animation).getObj()){
            case Constants.PLAYER_PRACTICING_FRAGMENT:{
                GraphicUtil.fadeIn(binding.bluetoothStateLayout, -1, null, 70, true);
                // start PlayerPracticingFragment
                transactionToFragment(this, PlayerPracticingFragment.class);
                break;
            }
            case Constants.BACK_TO_INIT_FRAGMENT:{
                GraphicUtil.fadeIn(binding.bluetoothStateLayout, -1, null, 70, true);

                // start StartingPlayerFragment
                transactionToFragment(this, StartingPlayerFragment.class);

                resetMyBluetoothStatus();
                break;
            }
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}

    @Override public void onBluetoothStateChangedEventReceived(Context context, Intent intent) {
        super.onBluetoothStateChangedEventReceived(context,intent);
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        if (state == BluetoothAdapter.STATE_ON) {// bluetooth on
            updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                    binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
            if (currentFragment == Constants.PLAYER_PRACTICING_FRAGMENT) {
                // torna a StartingPlayerFragment
                transactionToFragment(this, StartingPlayerFragment.class);
                resetMyBluetoothStatus();
            }
        }
    }

    // utils--------------------------------------------------------
    protected Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.PLAYER_STARTING_FRAGMENT:
                return StartingPlayerFragment.class;
            case Constants.PLAYER_PRACTICING_FRAGMENT:
                return PlayerPracticingFragment.class;
        }
        return StartingPlayerFragment.class;
    }

    protected String getInfoTextFromFragment(int currentFragment) {
        if (currentFragment == Constants.PLAYER_STARTING_FRAGMENT) {
            return getString(R.string.starting_player_info);
        }
        return null;
    }
}
