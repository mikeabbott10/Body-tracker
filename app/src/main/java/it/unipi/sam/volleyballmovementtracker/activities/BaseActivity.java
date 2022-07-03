package it.unipi.sam.volleyballmovementtracker.activities;

import android.os.Bundle;
import android.transition.Fade;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // we are adding fade animation
        // between two imageviews.
        Fade fade = new Fade();
        fade.setInterpolator(new DecelerateInterpolator());
        fade.setDuration(1000);

        // here also we are excluding status bar,
        // action bar and navigation bar from animation.
        //View decor = getWindow().getDecorView();
        //fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        //fade.excludeTarget(android.R.id.statusBarBackground, true);
        //fade.excludeTarget(android.R.id.navigationBarBackground, true);

        // we are adding fade animation
        // for enter transition.
        getWindow().setEnterTransition(fade);

        // we are also setting fade
        // animation for exit transition.
        getWindow().setExitTransition(fade);
    }
}
