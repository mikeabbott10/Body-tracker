package it.unipi.sam.bodymovementtracker.activities.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import it.unipi.sam.bodymovementtracker.util.Constants;

public class GUIBaseActivity extends AppCompatActivity {
    protected int whoAmIDrawableId;
    protected int currentTrainingDrawableId;
    protected int currentBtStateDrawableId;
    protected int currentVideoPlayerId;
    protected int mainColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // get custom instance state or original instance state
        Bundle startingThisActivityBundle = getIntent().getBundleExtra(Constants.starting_component_bundle_key);
        if(startingThisActivityBundle != null){
            super.onCreate(savedInstanceState);
            mainColor =
                    startingThisActivityBundle.getInt(Constants.choice_key, -1);
            whoAmIDrawableId =
                    startingThisActivityBundle.getInt(Constants.who_am_i_id_key, ResourcesCompat.ID_NULL);
            currentTrainingDrawableId =
                    startingThisActivityBundle.getInt(Constants.current_training_id_key, ResourcesCompat.ID_NULL);
            currentVideoPlayerId = ResourcesCompat.ID_NULL;
            currentBtStateDrawableId = ResourcesCompat.ID_NULL;
            //initTheme();
        }else if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
            mainColor =
                    savedInstanceState.getInt(Constants.choice_key, -1);
            whoAmIDrawableId =
                    savedInstanceState.getInt(Constants.who_am_i_id_key, ResourcesCompat.ID_NULL);
            currentTrainingDrawableId =
                    savedInstanceState.getInt(Constants.current_training_id_key, ResourcesCompat.ID_NULL);
            currentVideoPlayerId =
                    savedInstanceState.getInt(Constants.current_video_player_id_key, ResourcesCompat.ID_NULL);
            currentBtStateDrawableId =
                    savedInstanceState.getInt(Constants.current_bt_state_id_key, ResourcesCompat.ID_NULL);
            //initTheme();
        }else {
            super.onCreate(null);
            mainColor = -1;
            whoAmIDrawableId = ResourcesCompat.ID_NULL;
            currentTrainingDrawableId = ResourcesCompat.ID_NULL;
            currentVideoPlayerId = ResourcesCompat.ID_NULL;
            currentBtStateDrawableId = ResourcesCompat.ID_NULL;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.choice_key, mainColor);
        outState.putInt(Constants.who_am_i_id_key, whoAmIDrawableId);
        outState.putInt(Constants.current_training_id_key, currentTrainingDrawableId);
        outState.putInt(Constants.current_video_player_id_key, currentVideoPlayerId);
        outState.putInt(Constants.current_bt_state_id_key, currentBtStateDrawableId);
    }

    /*private void initTheme() {
        if(mainColor ==Constants.BLUE)
            setTheme(R.style.AppThemeBlueBackground);
        if(mainColor ==Constants.RED)
            setTheme(R.style.AppThemeRedBackground);
    }*/
}
