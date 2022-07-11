package it.unipi.sam.volleyballmovementtracker.activities;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.util.GUIBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.MyTranslateAnimation;

public class MainActivity extends GUIBaseActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String TAG = "AAAMainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initChoiceViews();

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

    private void initChoiceViews() {
        binding.choice1.choiceView.setOnClickListener(this);
        binding.choice2.choiceView.setOnClickListener(this);

        binding.choice1.choiceView.setBackgroundColor(getResources().getColor(R.color.MidnightBlue));
        binding.choice1.shapeIv.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.coach1));
        binding.choice1.choiceTv.setText(getString(R.string.coach));
        binding.choice1.choiceTvBack.setText(getString(R.string.coach));

        binding.choice2.choiceView.setBackgroundColor(getResources().getColor(R.color.Maroon));
        binding.choice2.shapeIv.setImageDrawable(
                AppCompatResources.getDrawable(this, R.drawable.block3));
        binding.choice2.choiceTv.setText(getString(R.string.player));
        binding.choice2.choiceTvBack.setText(getString(R.string.player));

        binding.choice1.shapeIv.setTransitionName(Constants.who_am_i_id_key);
        binding.choice2.shapeIv.setTransitionName(Constants.who_am_i_id_key);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            GraphicUtil.slideLeftToOrigin(binding.choice1.choiceTvLayout, -1, null, 1, true);
            GraphicUtil.slideRightToOrigin(binding.choice2.choiceTvLayout, -1, null, 1, true);
        } else {
            // code for landscape mode
            GraphicUtil.slideDownToOrigin(binding.choice1.choiceTvLayout, -1, null, 1, true);
            GraphicUtil.slideUpToOrigin(binding.choice2.choiceTvLayout, -1,null, 1, true);
        }
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
            if(view.getId() == binding.choice1.choiceView.getId()){
                // clicked choice 1
                GraphicUtil.slideLeft(binding.choice1.choiceTvLayout, Constants.COACH_CHOICE, this, 1, true);
                GraphicUtil.slideRight(binding.choice2.choiceTvLayout, -1, null, 1, true);
            }else{
                // clicked choice 2
                GraphicUtil.slideLeft(binding.choice2.choiceTvLayout, Constants.PLAYER_CHOICE, this, 1, true);
                GraphicUtil.slideRight(binding.choice1.choiceTvLayout, -1, null, 1, true);
            }

        } else {
            // code for landscape mode
            if(view.getId() == binding.choice1.choiceView.getId()){
                // clicked choice 1
                GraphicUtil.slideUp(binding.choice2.choiceTvLayout, Constants.COACH_CHOICE, this, 1, true);
                GraphicUtil.slideDown(binding.choice1.choiceTvLayout, -1, null, 1, true);
            }else{
                // clicked choice 2
                GraphicUtil.slideUp(binding.choice1.choiceTvLayout, Constants.PLAYER_CHOICE, this, 1, true);
                GraphicUtil.slideDown(binding.choice2.choiceTvLayout, -1, null, 1, true);
            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        int anim_obj = (int) ((MyTranslateAnimation)animation).getObj();
        if(anim_obj == Constants.COACH_CHOICE){
            // start coach activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.choice1.shapeIv, getString(R.string.who_am_i_choice));
            // start the new activity
            Intent i = new Intent(MainActivity.this, CoachPracticingActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.coach1);
            mBundle.putInt(Constants.choice_key, Constants.BLUE);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else if(anim_obj == Constants.PLAYER_CHOICE){
            // start player activity
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.choice2.shapeIv, getString(R.string.who_am_i_choice));

            // start the new activity
            Intent i = new Intent(this, PlayerPracticingActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.block3);
            mBundle.putInt(Constants.choice_key, Constants.RED);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
}