package it.unipi.sam.volleyballmovementtracker.activities.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import java.io.IOException;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.util.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPlayerBinding;

public class PlayerActivity extends SharedElementBaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, View.OnClickListener {
    private static final String TAG = "AAAPlayerActivity";
    private ActivityPlayerBinding binding;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private static final String VIDEO_PATH = "http://techslides.com/demos/sample-videos/small.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        Glide
            .with(this)
            .load(R.drawable.waiting)
            .into(binding.player.loadingIv);

        setContentView(binding.getRoot());

        surfaceHolder = binding.player.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        binding.player.surfaceView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        binding.player.container.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        binding.player.container.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        try{
            mediaPlayer.setDataSource(VIDEO_PATH);
            mediaPlayer.setOnPreparedListener(PlayerActivity.this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {}

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setPlayerSize();
        binding.player.loadingIv.setVisibility(View.GONE);
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // The MediaPlayer has moved to the Error state, must be reset!
        mediaPlayer.reset();
        surfaceCreated(surfaceHolder);
        return false;
    }

    // utils-----------------------------------------
    private void setPlayerSize() {
        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = binding.player.surfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }

        // Commit the layout parameters even to actions bar
        binding.player.actions.setLayoutParams(lp);
    }

    @NonNull
    private static RelativeLayout.LayoutParams copy(ViewGroup.LayoutParams viewLayoutParamsToCopy) {
        RelativeLayout.LayoutParams copiedParams = new RelativeLayout.LayoutParams(viewLayoutParamsToCopy);
        if (viewLayoutParamsToCopy instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relativeLayoutParamsToCopy = (RelativeLayout.LayoutParams) viewLayoutParamsToCopy;
            int[] rulesToCopy = relativeLayoutParamsToCopy.getRules();
            for (int verb = 0; verb < rulesToCopy.length; verb++) {
                int subject = rulesToCopy[verb];
                copiedParams.addRule(verb, subject);
            }
        }
        return copiedParams;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == binding.player.surfaceView.getId()){
            // todo fade in e fade out
            binding.player.actions.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.player.actions.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        }
    }
}
