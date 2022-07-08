package it.unipi.sam.volleyballmovementtracker.activities.coach;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.CoachPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.activities.util.GUIBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityCoachBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class CoachActivity extends GUIBaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String TAG = "AAACoachActivity";
    private ActivityCoachBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityCoachBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        setContentView(binding.getRoot());

        binding.centraleView.setOnClickListener(this);
        binding.centraleELateraleView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        binding.middleBlockerIv.setVisibility(View.VISIBLE);
        binding.middlePlusHitterIv.setVisibility(View.VISIBLE);
        super.onResume();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeftToOrigin(binding.middleTvLayout, -1, null);
            GraphicUtil.slideRightToOrigin(binding.middleHitterTvLayout, -1, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideDownToOrigin(binding.middleTvLayout, -1, null);
            GraphicUtil.slideUpToOrigin(binding.middleHitterTvLayout, -1,null);
        }
    }

    @Override
    public void onBackPressed() {
        binding.middleBlockerIv.setVisibility(View.INVISIBLE);
        binding.middlePlusHitterIv.setVisibility(View.INVISIBLE);
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

    @Override
    public void onClick(View view) {
        // start coach or player activity
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeft(binding.middleTvLayout,
                    view.getId() == binding.centraleView.getId()
                            ? Constants.STARTING_MB_TRAINING : Constants.STARTING_MB_HITTER_TRAINING,this);
            GraphicUtil.slideRight(binding.middleHitterTvLayout, -1, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideUp(binding.middleTvLayout,
                    view.getId() == binding.centraleView.getId()
                            ? Constants.STARTING_MB_TRAINING : Constants.STARTING_MB_HITTER_TRAINING, this);
            GraphicUtil.slideDown(binding.middleHitterTvLayout, -1, null);
        }
    }

    @Override
    public void onAnimationEnd(/*MyTranslateAnimation*/Animation animation) {
        // init intent
        Intent i = new Intent(CoachActivity.this, CoachPracticingActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putInt(Constants.who_am_i_id_key, whoAmIDrawableId);
        //init shared element options
        ActivityOptions options = null;
        if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_MB_TRAINING)){
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/current_training_choice"
            options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.middleBlockerIv, getString(R.string.current_training_choice));

            // start the new activity
            mBundle.putInt(Constants.current_training_id_key, R.drawable.block2);
        }else if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_MB_HITTER_TRAINING)){
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/current_training_choice"
            options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.middlePlusHitterIv, getString(R.string.current_training_choice));

            // start the new activity
            mBundle.putInt(Constants.current_training_id_key, R.drawable.block_plus);
        }

        // start the new activity
        i.putExtra(Constants.starting_component_bundle_key, mBundle);
        assert options != null;
        startActivity(i, options.toBundle());
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}