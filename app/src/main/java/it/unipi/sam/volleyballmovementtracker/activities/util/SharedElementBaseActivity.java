package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class SharedElementBaseActivity extends DialogActivity {
    protected int whoAmIDrawableId;
    protected int currentTrainingDrawableId;
    protected int currentBtStateDrawableId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // get custom instance state or original instance state
        Bundle startingThisActivityBundle = getIntent().getBundleExtra(Constants.starting_component_bundle_key);
        if(startingThisActivityBundle != null){
            super.onCreate(savedInstanceState);
            whoAmIDrawableId = startingThisActivityBundle.getInt(Constants.who_am_i_id_key);
            currentTrainingDrawableId = startingThisActivityBundle.getInt(Constants.current_training_id_key);
            currentBtStateDrawableId = R.drawable.ic_ok;
        }else if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
            whoAmIDrawableId = savedInstanceState.getInt(Constants.who_am_i_id_key);
            currentTrainingDrawableId = savedInstanceState.getInt(Constants.current_training_id_key);
            currentBtStateDrawableId = savedInstanceState.getInt(Constants.current_bt_state_id_key);
        }else {
            super.onCreate(null);
            whoAmIDrawableId = R.drawable.ic_home_black_24dp; // no reason (only launcher activity here: it doesn't use this var)
            currentTrainingDrawableId = R.drawable.ic_home_black_24dp; // no reason (only launcher activity here: it doesn't use this var)
            currentBtStateDrawableId = R.drawable.ic_ok;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.who_am_i_id_key, whoAmIDrawableId);
        outState.putInt(Constants.current_training_id_key, currentTrainingDrawableId);
        outState.putInt(Constants.current_bt_state_id_key, currentBtStateDrawableId);
    }
}
