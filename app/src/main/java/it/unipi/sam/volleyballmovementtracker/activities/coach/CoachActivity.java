package it.unipi.sam.volleyballmovementtracker.activities.coach;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.MiddleAndHitterBlockActivity;
import it.unipi.sam.volleyballmovementtracker.activities.coach.practices.MiddleBlockerBlockActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityCoachBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class CoachActivity extends SharedElementBaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String TAG = "AAACoachActivity";
    private ActivityCoachBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        super.onResume();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeftToOrigin(binding.middleTvLayout, null, null);
            GraphicUtil.slideRightToOrigin(binding.middleHitterTvLayout, null, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideDownToOrigin(binding.middleTvLayout, null, null);
            GraphicUtil.slideUpToOrigin(binding.middleHitterTvLayout, null,null);
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
            GraphicUtil.slideLeft(binding.middleTvLayout,
                    view.getId() == binding.centraleView.getId()
                            ? Constants.STARTING_MB_ACTIVITY : Constants.STARTING_MB_HITTER_ACTIVITY,this);
            GraphicUtil.slideRight(binding.middleHitterTvLayout, null, null);
        } else {
            // code for landscape mode
            GraphicUtil.slideUp(binding.middleTvLayout,
                    view.getId() == binding.centraleView.getId()
                            ? Constants.STARTING_MB_ACTIVITY : Constants.STARTING_MB_HITTER_ACTIVITY, this);
            GraphicUtil.slideDown(binding.middleHitterTvLayout, null, null);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_MB_ACTIVITY)){
            // start trainer activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/current_training_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.middleBlockerIv, getString(R.string.current_training_choice));
            // start the new activity
            Intent i = new Intent(CoachActivity.this, MiddleBlockerBlockActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, whoAmIDrawableId);
            mBundle.putInt(Constants.current_training_id_key, R.drawable.ic_block2);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else if(((MyTranslateAnimation)animation).getObj().equals(Constants.STARTING_MB_HITTER_ACTIVITY)){
            // start player activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/current_training_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.middlePlusHitterIv, getString(R.string.current_training_choice));

            // start the new activity
            Intent i = new Intent(this, MiddleAndHitterBlockActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, whoAmIDrawableId);
            mBundle.putInt(Constants.current_training_id_key, R.drawable.ic_blockplus);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}