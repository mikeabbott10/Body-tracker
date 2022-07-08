package it.unipi.sam.volleyballmovementtracker.activities.player;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.player.fragments.StartingPlayerFragment;
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
        binding.middleActionIconIv.setImageDrawable( // it's video playlist icon here
                AppCompatResources.getDrawable(this, currentVideoPlayerId));
        binding.bluetoothState.setImageDrawable(
                AppCompatResources.getDrawable(this, currentBtStateDrawableId));

        setContentView(binding.getRoot());
        showMyDialog(showingDialog);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment!=Constants.PLAYER_STARTING_FRAGMENT){
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
            switch (currentFragment) {
                case Constants.PLAYER_STARTING_FRAGMENT:
                case Constants.PLAYER_PRACTICING_FRAGMENT:
                    binding.middleActionIconIv.setImageDrawable( // it's video playlist icon here
                            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_video_library_24));
                    break;
                case Constants.VIDEO_PLAYER_PLAYLIST_FRAGMENT:
                    binding.middleActionIconIv.setImageDrawable( // it's video playlist icon here
                            AppCompatResources.getDrawable(this, R.drawable.ic_colored_baseline_video_library_24));
                    break;
            }
        }
    }

    // activity views onclick
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==binding.middleActionIconIv.getId()){
            GraphicUtil.fadeOut(binding.middleActionIconIv,
                    Constants.VIDEO_PLAYER_PLAYLIST_FRAGMENT, this,300);
        }else if(view.getId()==binding.bluetoothState.getId()){
            GraphicUtil.scaleImage(this, view, -1, null);
        }
    }

    // animation end usata come trigger per l'esecuzione di operazioni come il replace di fragment
    @Override
    public void onAnimationEnd(/*MyAlphaAnimation*/Animation animation) {
        switch((int)((MyAlphaAnimation)animation).getObj()){
            case Constants.PLAYER_PRACTICING_FRAGMENT:{
                /*GraphicUtil.fadein(binding.bluetoothStateLayout, -1, null, 300);
                // start PlayerPracticingFragment
                transactionToFragment(this, PlayerPracticingFragment.class);
                break;*/
            }
            case Constants.BACK_TO_INIT_FRAGMENT:{
                GraphicUtil.fadeIn(binding.bluetoothStateLayout, -1, null, 300);

                // start StartingPlayerFragment
                transactionToFragment(this, StartingPlayerFragment.class);

                resetMyBluetoothStatus();
                break;
            }
            case Constants.VIDEO_PLAYER_PLAYLIST_FRAGMENT:{
                /*GraphicUtil.fadeIn(binding.videoPlayerBtn, -1, null, 300);
                // start VideoPlayerPlaylistFragment
                transactionToFragment(this, VideoPlayerPlaylistFragment.class);
                //todo usa la startActivity in VideoPlayerPlaylistFragment ( click su item di recyclerview (?) )
                /*
                // start video player
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra(Constants.play_this_video_key, Constants.MIDDLE_BLOCK_VIDEO);
                startActivity(intent);
                */
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
                // se ero connesso/paired è necessario tornare a PLAYER_STARTING_FRAGMENT
                // altrimenti resta (ma anche non restare)
            }
        }
    }

    // utils--------------------------------------------------------
    protected Class<? extends Fragment> getFragmentClassFromModel() {
        switch(currentFragment){
            case Constants.PLAYER_STARTING_FRAGMENT:
                return StartingPlayerFragment.class;
            /*case Constants.PLAYER_PRACTICING_FRAGMENT:
                return PlayerPracticingFragment.class;
            case Constants.VIDEO_PLAYER_PLAYLIST_FRAGMENT:
                return VideoPlayerPlaylistFragment.class;*/
        }
        return StartingPlayerFragment.class;
    }
}
