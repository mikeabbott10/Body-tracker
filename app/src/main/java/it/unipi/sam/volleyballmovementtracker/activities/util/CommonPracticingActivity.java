package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPracticingBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.PreferenceUtils;
import it.unipi.sam.volleyballmovementtracker.util.ResourcePreferenceWrapper;
import it.unipi.sam.volleyballmovementtracker.util.Training;
import it.unipi.sam.volleyballmovementtracker.util.download.DMRequestWrapper;
import it.unipi.sam.volleyballmovementtracker.util.download.JacksonUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class CommonPracticingActivity extends DownloadActivity implements RequestListener<Drawable>, View.OnClickListener, Observer<Object>,
        Animation.AnimationListener {
    private static final String TAG = "AAAACommPracActivity";
    protected int currentFragment;
    protected ActivityPracticingBinding binding;
    private List<Training> trainingsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mReceiver = new MyBroadcastReceiver(this, this);
        registerReceiver(mReceiver, myIntentFilter);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getCurrentFragment().observe(this, this);

        // ask for restInfo
        // rest info file is always downloaded at least once
        if(restInfoInstance==null){
            getRestInfoFileFromNet(new DMRequestWrapper(Constants.restBasePath + Constants.firstRestReqPath,
                    "notUseful", "notUseful", false, false, REST_INFO_JSON,
                    false, null, null));
        }

        initDialog();
        initBinding();
        initInfoView();
    }

    protected void initBinding(){
        binding = ActivityPracticingBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setOnClickListener(this);
        binding.middleActionIconIv.setOnClickListener(this);
        binding.bluetoothState.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        binding.fragmentContainerMain.setVisibility(View.VISIBLE);
        // While your activity is in the STARTED lifecycle state
        // or higher, fragments can be added, replaced, or removed
        transactionToFragment(this, getFragmentClassFromModel());
    }

    @Override
    public void onBackPressed() {
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

    // override this, call super
    @Override public void onClick(View view) {
        if(view.getId()==binding.backBtn.getId()){
            onBackPressed();
        }else if(view.getId()==binding.whoAmIIv.getId()){
            GraphicUtil.scaleImage(this, view, -1, null);
        }

        // click on info_btn
        if (view.getId() == binding.infoBtn.getId()) {
            PreferenceUtils.setShowGesture(this, false);
            if (binding.infoDescription.getVisibility() != View.VISIBLE) {
                // make description visible
                binding.infoBtn.setAlpha(0.5f);
                GraphicUtil.slideDownToOrigin(binding.infoDescription, -1, null);
                binding.infoDescription.setVisibility(View.VISIBLE);
            } else {
                // make description invisible
                binding.infoBtn.setAlpha(1.0f);
                GraphicUtil.slideUp(binding.infoDescription, -1, null);
                binding.infoDescription.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override public void onBluetoothStateChangedEventReceived(Context context, Intent intent) {
        super.onBluetoothStateChangedEventReceived(context,intent);
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        if (state == BluetoothAdapter.STATE_OFF) {
            // change icon
            updateBtIcon(binding.bluetoothState, R.drawable.disabled_bt,
                    binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
        }
    }

    @Override
    protected void handleResponseUri(long dm_resource_id, Integer type, String uriString, long lastModifiedTimestamp, boolean updateResourcePreference) {
        super.handleResponseUri(dm_resource_id, type, uriString, lastModifiedTimestamp, updateResourcePreference);
        switch(type) {
            case TRAININGS_JSON: {
                // update resource preference if needed
                if(updateResourcePreference)
                    PreferenceUtils.setResourceUri(this, Constants.trainings_rest_key+type, uriString, lastModifiedTimestamp, dm_resource_id);

                if(!updateResourcePreference && trainingsList!=null && trainingsList.size()>0){
                    // se non devo salvare la Preference e trainingsList è già una lista con elementi,
                    // allora è inutile aggiornare trainingsList con sè stessa
                    break;
                }
                String content = getFileContentFromUri(uriString);
                // perform jackson from file to object
                try {
                    Training[] t_arr = (Training[]) JacksonUtil.getObjectFromString(content, Training[].class);
                    trainingsList = new ArrayList<>(Arrays.asList(t_arr));
                    Log.d(TAG, "handleResponseUri. trainingsList:" + trainingsList);

                    // populate news in news fragment
                    Collections.sort(trainingsList); // date sort
                    viewModel.selectTrainingList(trainingsList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    Snackbar.make(binding.getRoot(), "ERROR 00. Please retry later.", 5000).show();
                }
                break;
            }
            case REST_INFO_JSON: {
                getTrainings();
                break;
            }
        }
    }

    @Override
    protected void handle404(long dm_resource_id, Integer type, String uriString) {
        super.handle404(dm_resource_id, type, uriString);
        if (type == null) {
            Snackbar.make(binding.getRoot(), "ERROR 01. Please retry later.", 5000).show();
            return;
        }
        switch (type) {
            case TRAININGS_JSON:
                Snackbar.make(binding.getRoot(), "ERROR 02. Please retry later.", 5000).show();
                break;
            case REST_INFO_JSON:
                Snackbar.make(binding.getRoot(), "ERROR 03. Please retry later.", 5000).show();
                break;
        }
    }

    // override these
    protected Class<? extends Fragment> getFragmentClassFromModel() {return null;}
    @Override public void onAnimationStart(Animation animation) {}
    @Override public void onAnimationEnd(Animation animation) {}
    @Override public void onAnimationRepeat(Animation animation) {}
    @Override public void onChanged(Object o) {}


    // info view stuff ------------------------------------------------
    protected void initInfoView(){
        if(PreferenceUtils.getShowGesture(this)) {
            Glide
                    .with(this)
                    .load(R.drawable.info_tap_animated)
                    .listener(this)
                    .into(binding.infoTapGifIv);
        }
        binding.infoBtn.setOnClickListener(this);
    }

    protected void setInfoText(String info_text){
        binding.infoDescription.setText(info_text);
    }

    // glide related callbacks
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        ((GifDrawable)resource).setLoopCount(2);
        ((GifDrawable)resource).registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                //do whatever after specified number of loops complete
                ((GifDrawable)resource).stop();
                binding.infoTapGifIv.setVisibility(View.GONE);
            }
        });
        return false;
    }

    // utils-----------------------------------------------------------------
    private void getTrainings() {
        Map<String, Object> riiMap = restInfoInstance.getLastModified().get( Constants.trainings_rest_key );
        ResourcePreferenceWrapper trainingsJsonPreference = null;
        if(riiMap!=null)
            trainingsJsonPreference = PreferenceUtils.getResourceUri(this, Constants.trainings_rest_key+TRAININGS_JSON, (Integer) riiMap.get( Constants.trainings_rest_key ));

        Log.d(TAG, "getTrainings. trainingsJsonPreference:"+trainingsJsonPreference);
        if(trainingsJsonPreference!=null && trainingsJsonPreference.getUri()!=null) {
            Log.d(TAG, "getTrainings. From local");
            handleResponseUri(trainingsJsonPreference.getDMResourceId(), TRAININGS_JSON,
                    trainingsJsonPreference.getUri(), trainingsJsonPreference.getLastModifiedTimestamp(), false);
        }else {
            Log.d(TAG, "getTrainings. From net");
            getTrainingsInfoFileFromNet(
                    new DMRequestWrapper(Constants.restBasePath + restInfoInstance.getTrainingsUrl(),
                            "randomTitle", "randomDescription", false, false, TRAININGS_JSON,
                            false, null, null)
            );
        }
    }

    protected String getInfoTextFromFragment(int currentFragment) {
        switch(currentFragment){
            // coach fragments
            case Constants.PICKER_FRAGMENT:
                return getString(R.string.picker_fragment_info);
            case Constants.SELECT_TRAINING_FRAGMENT:
                return getString(R.string.select_training_fragment_info);
            case Constants.GET_CONNECTIONS_FRAGMENT:
                return getString(R.string.get_connections_fragment_info);

            // player fragments ...

        }
        return null;
    }

    protected static void transactionToFragment(Context ctx, Class<? extends Fragment> clas) {
        FragmentManager fragmentManager = ((FragmentActivity)ctx).getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, 0)
                .replace(R.id.fragment_container_main, clas, null)
                .setReorderingAllowed(true)
                .commit();
    }

    protected void updateBtIcon(ImageView iconView, int iconId,
                                ImageView overlayView, int overlayId, boolean isOverlayGif){
        iconView.setImageDrawable(
                AppCompatResources.getDrawable(this, iconId));
        if(isOverlayGif){
            Glide
                    .with(this)
                    .load(overlayId)
                    .into(overlayView);
            return;
        }
        overlayView.setImageDrawable( overlayId == ResourcesCompat.ID_NULL ? null :
                AppCompatResources.getDrawable(this, overlayId));
    }
}
