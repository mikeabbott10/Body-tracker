package it.unipi.sam.volleyballmovementtracker.activities.base;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import it.unipi.sam.volleyballmovementtracker.activities.fragments.SelectTrainingFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPracticingBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.PreferenceUtils;
import it.unipi.sam.volleyballmovementtracker.util.ResourcePreferenceWrapper;
import it.unipi.sam.volleyballmovementtracker.util.Training;
import it.unipi.sam.volleyballmovementtracker.util.bluetooth.OnBroadcastReceiverOnBTReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.download.DMRequestWrapper;
import it.unipi.sam.volleyballmovementtracker.util.download.JacksonUtil;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public abstract class CommonPracticingActivity extends ServiceBTActivity implements
        RequestListener<Drawable>, View.OnClickListener, OnBroadcastReceiverOnBTReceiveListener {
    private static final String TAG = "AAAACommPracActivity";
    protected int currentFragment;
    protected ActivityPracticingBinding binding;
    private List<Training> trainingsList;
    protected MyViewModel viewModel;
    protected MyBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initBars();
        initInfoView();

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // ask for restInfo
        // rest info file is always downloaded at least once
        if(restInfoInstance==null){
            getRestInfoFileFromNet(new DMRequestWrapper(Constants.restBasePath + Constants.firstRestReqPath,
                    "notUseful", "notUseful", false,
                    false, REST_INFO_JSON,
                    false, null, null));
        }

        viewModel.getCurrentFragment().observe(this, fragment_id -> {
            currentFragment = fragment_id;
            setInfoText(getInfoTextFromFragment(currentFragment));
        });

        // get bt state even without service on
        myIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mReceiver = new MyBroadcastReceiver(this, this);
    }
    protected abstract String getInfoTextFromFragment(int currentFragment);

    @Override
    protected void onResume() {
        super.onResume();
        //binding.fragmentContainerMain.setVisibility(View.VISIBLE);
        // While your activity is in the STARTED lifecycle state
        // or higher, fragments can be added, replaced, or removed
        transactionToFragment(this, getFragmentClassFromModel(), false);
    }
    protected abstract Class<? extends Fragment> getFragmentClassFromModel();

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
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
                GraphicUtil.slideDownToOrigin(binding.infoDescription, -1, null, 2, false);
                binding.infoDescription.setVisibility(View.VISIBLE);
            } else {
                // make description invisible
                binding.infoBtn.setAlpha(1.0f);
                GraphicUtil.slideUp(binding.infoDescription, -1, null, 2, false);
                binding.infoDescription.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void handleServiceStateChange(int serviceState){
        switch (serviceState){
            case Constants.STARTING_SERVICE:{
                break;
            }
            case Constants.CLOSING_SERVICE:{
                break;
            }
        }
    }

    protected void handleServiceBTStateChange(int bt_state){
        switch(bt_state){
            case Constants.BT_STATE_DISABLED:
            case Constants.BT_STATE_UNSOLVED: {
                updateBtIcon(binding.bluetoothState, R.drawable.disabled_bt,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
            case Constants.BT_STATE_ENABLED:
            case Constants.BT_STATE_CONNECTION_FAILED:
            case Constants.BT_STATE_JUST_DISCONNECTED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
            case Constants.BT_STATE_DISCOVERABLE_AND_LISTENING:
            case Constants.BT_STATE_DISCOVERING:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.waiting, true);
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.ic_disabled_ok, false);
                break;
            }
            case Constants.BT_STATE_BADLY_DENIED:
            case Constants.BT_STATE_PERMISSION_REQUIRED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.ic_wrong, false);
                break;
            }
        }
    }

    @Override
    protected void handleResponseUri(long dm_resource_id, Integer type, String uriString,
                                     long lastModifiedTimestamp, boolean updateResourcePreference) {
        super.handleResponseUri(dm_resource_id, type, uriString, lastModifiedTimestamp, updateResourcePreference);
        switch(type) {
            case TRAININGS_JSON: {
                // update resource preference if needed
                if(updateResourcePreference)
                    PreferenceUtils.setResourceUri(this, Constants.trainings_rest_key+type, uriString,
                            lastModifiedTimestamp, dm_resource_id);

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
                    //Log.d(TAG, "handleResponseUri. trainingsList:" + trainingsList);

                    // populate news in news fragment
                    Collections.sort(trainingsList); // date sort
                    viewModel.selectTrainingList(trainingsList);
                } catch (JsonProcessingException e) {
                    Log.e(TAG, "", e);
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

    @Override
    public void onBluetoothStateChangedEventReceived(int state) {
        switch(state){
            case BluetoothAdapter.STATE_OFF:{
                updateBtIcon(binding.bluetoothState, R.drawable.disabled_bt,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
            case BluetoothAdapter.STATE_ON:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
        }
    }

    // unused here
    @Override public void onBluetoothScanModeChangedEventReceived(int scanMode) {}

    // utils-----------------------------------------------------------------
    private void getTrainings() {
        Map<String, Object> riiMap = restInfoInstance.getLastModified().get( Constants.trainings_rest_key );
        ResourcePreferenceWrapper trainingsJsonPreference = null;
        if(riiMap!=null)
            trainingsJsonPreference = PreferenceUtils.getResourceUri(this,
                    Constants.trainings_rest_key+TRAININGS_JSON,
                    (Integer) riiMap.get( Constants.trainings_rest_key ));

        //Log.d(TAG, "getTrainings. trainingsJsonPreference:"+trainingsJsonPreference);
        if(trainingsJsonPreference!=null && trainingsJsonPreference.getUri()!=null) {
            Log.d(TAG, "getTrainings. From local");
            handleResponseUri(trainingsJsonPreference.getDMResourceId(), TRAININGS_JSON,
                    trainingsJsonPreference.getUri(), trainingsJsonPreference.getLastModifiedTimestamp(),
                    false);
        }else {
            Log.d(TAG, "getTrainings. From net");
            getTrainingsInfoFileFromNet(
                    new DMRequestWrapper(Constants.restBasePath + restInfoInstance.getTrainingsUrl(),
                            "randomTitle", "randomDescription", false,
                            false, TRAININGS_JSON,
                            false, null, null)
            );
        }
    }

    public Class<? extends Fragment> getCurrentFragment(){
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container_main);
        if(f==null) return null;
        return f.getClass();
    }

    protected void transactionToFragment(Context ctx, Class<? extends Fragment> clas, boolean addToBackStack) {
        if(getCurrentFragment()==clas)
            return;
        FragmentManager fragmentManager = ((FragmentActivity)ctx).getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, 0)
                .replace(R.id.fragment_container_main, clas, null)
                .setReorderingAllowed(true);
        if(addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
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

    protected void updateBtIconWithCurrentState(boolean state) {
        onBluetoothStateChangedEventReceived(state ?
                BluetoothAdapter.STATE_ON:BluetoothAdapter.STATE_OFF);
    }

    private void initBars() {
        assert mainColor != -1;
        if(mainColor == Constants.BLUE) {
            binding.topBar.setBackgroundColor(getResources().getColor(R.color.MidnightBlue));
            binding.bottomBar.setBackgroundColor(getResources().getColor(R.color.MidnightBlue));
        }
        if(mainColor ==Constants.RED) {
            binding.topBar.setBackgroundColor(getResources().getColor(R.color.Maroon));
            binding.bottomBar.setBackgroundColor(getResources().getColor(R.color.Maroon));
        }
    }

    protected void initBinding(){
        binding = ActivityPracticingBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setOnClickListener(this);
        binding.middleActionIconIv.setOnClickListener(this);
        binding.bluetoothState.setOnClickListener(this);
        binding.backBtn.setOnClickListener(this);
    }

    public void myStopServiceAndGoBack() {
        super.myStopService();
        transactionToFragment(this, SelectTrainingFragment.class, false);
    }

    // info view stuff --------------
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
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource,
                                   boolean isFirstResource) {
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
}
