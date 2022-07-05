package it.unipi.sam.volleyballmovementtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.activities.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.activities.coach.CoachActivity;
import it.unipi.sam.volleyballmovementtracker.activities.player.PlayerActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class MainActivity extends SharedElementBaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String TAG = "AAAMainActivity";
    private ActivityMainBinding binding;
    private BroadcastReceiver mReceiver;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.trainerView.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);

        // check bt support

        BluetoothAdapter bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(bta==null){
            // device doesn't support bt
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.bt_not_supported))
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> finish())
                    .create().show();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeftToOrigin(binding.trainerTvLayout, -1, null);
            GraphicUtil.slideRightToOrigin(binding.playerTvLayout, -1, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideDownToOrigin(binding.trainerTvLayout, -1, null);
            GraphicUtil.slideUpToOrigin(binding.playerTvLayout, -1,null);
        }
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
        // start coach or player activity
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeft(binding.trainerTvLayout,
                    view.getId() == binding.trainerView.getId()
                            ? Constants.STARTING_COACH_ACTIVITY : Constants.STARTING_PLAYER_ACTIVITY,this);
            GraphicUtil.slideRight(binding.playerTvLayout, -1, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideUp(binding.trainerTvLayout,
                    view.getId() == binding.trainerView.getId()
                            ? Constants.STARTING_COACH_ACTIVITY : Constants.STARTING_PLAYER_ACTIVITY, this);
            GraphicUtil.slideDown(binding.playerTvLayout, -1, null);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        int anim_obj = (int) ((MyTranslateAnimation)animation).getObj();
        if(anim_obj == Constants.STARTING_COACH_ACTIVITY){
            // start coach activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.coachIv, getString(R.string.who_am_i_choice));
            // start the new activity
            Intent i = new Intent(MainActivity.this, CoachActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_coach1);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else if(anim_obj == Constants.STARTING_PLAYER_ACTIVITY){
            // start player activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.playerIv, getString(R.string.who_am_i_choice));

            // start the new activity
            Intent i = new Intent(this, PlayerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_block3);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}