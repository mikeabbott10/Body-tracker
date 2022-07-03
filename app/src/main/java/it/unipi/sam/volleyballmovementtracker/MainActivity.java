package it.unipi.sam.volleyballmovementtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.activities.BaseActivity;
import it.unipi.sam.volleyballmovementtracker.activities.player.PlayerActivity;
import it.unipi.sam.volleyballmovementtracker.activities.coach.CoachActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;
import pub.devrel.easypermissions.EasyPermissions;


// TODO:
//  0. scrivere mail a gervasi
//  1. porta vectors xml ad al massimo 200x200
public class MainActivity extends BaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String TAG = "AAAMainActivity";
    private ActivityMainBinding binding;
    private BroadcastReceiver mReceiver;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.trainerView.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);

        // check bt support
        if(bta==null){
            // device doesn't support bt
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.bt_not_supported))
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialogInterface, i) -> finish())
                    .create().show();
            return;
        }

        // check bt permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request), Constants.BT_PERMISSION_CODE, Constants.BT_PERMISSIONS);
            } else {
                bta.enable();
            }
        }else
            bta.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeftToOrigin(binding.trainerTvLayout, null, null);
            GraphicUtil.slideRightToOrigin(binding.playerTvLayout, null, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideDownToOrigin(binding.trainerTvLayout, null, null);
            GraphicUtil.slideUpToOrigin(binding.playerTvLayout, null,null);
        }
    }

    @Override
    protected void onDestroy() {
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
            GraphicUtil.slideRight(binding.playerTvLayout, null, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideUp(binding.trainerTvLayout,
                    view.getId() == binding.trainerView.getId()
                            ? Constants.STARTING_COACH_ACTIVITY : Constants.STARTING_PLAYER_ACTIVITY, this);
            GraphicUtil.slideDown(binding.playerTvLayout, null, null);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_COACH_ACTIVITY)){
            // start trainer activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.coachIv, getString(R.string.who_am_i_choice));
            // start the new activity
            Intent i = new Intent(MainActivity.this, CoachActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_coach1);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_PLAYER_ACTIVITY)){
            // start player activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.playerIv, getString(R.string.who_am_i_choice));

            // start the new activity
            Intent i = new Intent(this, PlayerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_block3);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}