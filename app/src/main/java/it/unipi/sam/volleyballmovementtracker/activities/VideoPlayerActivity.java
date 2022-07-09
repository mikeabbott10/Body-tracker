package it.unipi.sam.volleyballmovementtracker.activities;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import java.io.IOException;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityVideoPlayerBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, View.OnClickListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "AAAVideoPlayerActivity";
    private ActivityVideoPlayerBinding binding;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoUrl = getIntent().getStringExtra(Constants.play_this_video_key);
        Log.d(TAG, videoUrl);
        if(videoUrl==null){
            finish();
            return;
        }

        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());

        Glide
                .with(this)
                .load(R.drawable.waiting)
                .into(binding.player.loadingIv);

        setContentView(binding.getRoot());

        surfaceHolder = binding.player.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        binding.player.surfaceView.setOnClickListener(this);

        binding.player.playPauseBtn.setOnClickListener(this);
        binding.player.fastForwardBtn.setOnClickListener(this);
        binding.player.fastRewindBtn.setOnClickListener(this);

        binding.player.container.setOnClickListener(this);
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
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setOnPreparedListener(VideoPlayerActivity.this);
            mediaPlayer.setOnCompletionListener(VideoPlayerActivity.this);
            // mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); deprecated
            mediaPlayer.setAudioAttributes(new AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
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
    public void onCompletion(MediaPlayer mediaPlayer) {
        binding.player.playPauseBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_play_arrow_24));
        binding.player.actions.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        // The MediaPlayer has moved to the Error state, must be reset!
        mediaPlayer.reset();
        surfaceCreated(surfaceHolder);
        return false;
    }

    @Override
    public void onClick(View view) {
        // player container
        if(view.getId() == binding.player.container.getId()){
            super.onBackPressed();
            return;
        }

        // player actions container
        if(view.getId() == binding.player.surfaceView.getId()){
            if( ! showActionsIfGone())
                hideActionsIfVisible();
            return;
        }

        // player actions
        if(binding.player.actions.getVisibility() == View.VISIBLE) {
            if (view.getId() == binding.player.playPauseBtn.getId() && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                binding.player.playPauseBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_play_arrow_24));
            } else if (view.getId() == binding.player.playPauseBtn.getId() && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                binding.player.playPauseBtn.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_pause_24));
                hideActionsIfVisible();
            }

            if (view.getId() == binding.player.fastRewindBtn.getId()) {
                seekToFromStep(-1000);
            } else if (view.getId() == binding.player.fastForwardBtn.getId()) {
                seekToFromStep(1000);
            }
        }else
            showActionsIfGone();
    }

    // utils-----------------------------------------
    private void setPlayerSize() {
        // // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
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

    private boolean showActionsIfGone(){
        if(binding.player.actions.getVisibility() == View.GONE) {
            GraphicUtil.fadeIn(binding.player.actions, -1, null, -1, true);
            binding.player.actions.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void hideActionsIfVisible(){
        if(binding.player.actions.getVisibility() == View.VISIBLE) {
            GraphicUtil.fadeOut(binding.player.actions, -1, null, -1, true);
            binding.player.actions.setVisibility(View.GONE);
        }
    }

    public void seekToFromStep(int step) {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int seekPosition = Math.min(currentPosition + step, mediaPlayer.getDuration());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                mediaPlayer.seekTo(seekPosition, MediaPlayer.SEEK_CLOSEST);
            else
                mediaPlayer.seekTo(seekPosition);
        }
    }
}
