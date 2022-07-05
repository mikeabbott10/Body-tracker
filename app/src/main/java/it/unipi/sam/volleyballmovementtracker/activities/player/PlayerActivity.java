package it.unipi.sam.volleyballmovementtracker.activities.player;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import it.unipi.sam.volleyballmovementtracker.activities.util.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPlayerBinding;

public class PlayerActivity extends SharedElementBaseActivity{
    private static final String TAG = "AAAPlayerActivity";
    private ActivityPlayerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        setContentView(binding.getRoot());

        String src = "rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";
        binding.videoView.setVideoURI(Uri.parse(src));
        binding.videoView.setMediaController(new MediaController(this));
        binding.videoView.requestFocus();
        binding.videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
