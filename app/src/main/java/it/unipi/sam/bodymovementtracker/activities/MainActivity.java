package it.unipi.sam.bodymovementtracker.activities;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.activities.base.DialogActivity;
import it.unipi.sam.bodymovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.bodymovementtracker.services.SensorService;
import it.unipi.sam.bodymovementtracker.util.Constants;
import it.unipi.sam.bodymovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.bodymovementtracker.util.graphic.MyTranslateAnimation;

public class MainActivity extends DialogActivity implements View.OnClickListener,
        Animation.AnimationListener {
    private static final String TAG = "AAAMainActivity";
    private ActivityMainBinding binding;

    // check service state
    protected ServiceConnection mConnection;
    protected SensorService mBoundService;

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
            return;
        }

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = ((SensorService.LocalBinder)service).getService();
                if(mBoundService.isStarted){
                    unbindService(this);
                    showMyDialog(Constants.WORK_IN_PROGRESS_DIALOG);
                    return;
                }
                unbindService(this);
                mBoundService = null;
            }
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
            }
        };

        showMyDialog(showingDialog);
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

        if (bindService(new Intent(this, SensorService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
        } else {
            Log.e(TAG, "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
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

    // dialog onclick
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if(showingDialog == Constants.WORK_IN_PROGRESS_DIALOG) {
            // this activity is bound to the started service here
            if(i==RESULT_OK){
                // "Accept" is clicked on workInProgressDialog.
                if(mBoundService!=null)
                    mBoundService.myStop();
            }else{
                // go to practicing activity if service still started
                try {
                    if (mBoundService.isStarted)
                        startPracticingActivity(mBoundService.role == Constants.COACH_CHOICE);
                }catch(Exception e){
                    Log.w(TAG, "trying to artificially go to practicing activity with no service", e);
                }
            }
        }
        super.onClick(dialogInterface,i);
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
            startPracticingActivity(true);
        }else if(anim_obj == Constants.PLAYER_CHOICE){
            // start player activity
            startPracticingActivity(false);
        }
    }
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}

    private void startPracticingActivity(boolean isCoach){
        if(isCoach) {
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.choice1.shapeIv,
                            getString(R.string.who_am_i_choice));
            // start the new activity
            Intent i = new Intent(MainActivity.this, CoachPracticingActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.coach1);
            mBundle.putInt(Constants.choice_key, Constants.BLUE);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else{
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="@string/who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.choice2.shapeIv,
                            getString(R.string.who_am_i_choice));

            // start the new activity
            Intent i = new Intent(this, PlayerPracticingActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.block3);
            mBundle.putInt(Constants.choice_key, Constants.RED);
            i.putExtra(Constants.starting_component_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }
    }
}