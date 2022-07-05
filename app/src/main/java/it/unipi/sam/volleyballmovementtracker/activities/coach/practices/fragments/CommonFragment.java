package it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.PreferenceUtils;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class CommonFragment extends Fragment implements RequestListener<Drawable>, View.OnClickListener {
    private View infoTapGifIv;
    private View infoBtn;
    private View infoDescription;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void initInstanceState(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
        }else {
            super.onCreate(null);
        }
    }

    protected void initInfoView(ImageView infoTapGifIv, ImageButton infoBtn, TextView infoDescription){
        this.infoTapGifIv = infoTapGifIv;
        this.infoBtn = infoBtn;
        this.infoDescription = infoDescription;
        if(PreferenceUtils.getShowGesture(requireActivity())) {
            Glide
                    .with(this)
                    .load(R.drawable.info_tap_animated)
                    .listener(this)
                    .into(infoTapGifIv);
        }
        infoBtn.setOnClickListener(this);
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
                if(infoTapGifIv!=null)
                    infoTapGifIv.setVisibility(View.GONE);
            }
        });
        return false;
    }

    // click on info_btn
    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == infoBtn.getId()) {
                PreferenceUtils.setShowGesture(requireActivity(), false);
                if (infoDescription.getVisibility() != View.VISIBLE) {
                    // make description visible
                    infoBtn.setAlpha(0.5f);
                    GraphicUtil.slideDownToOrigin(infoDescription, -1, null);
                    infoDescription.setVisibility(View.VISIBLE);
                } else {
                    // make description invisible
                    infoBtn.setAlpha(1.0f);
                    GraphicUtil.slideUp(infoDescription, -1, null);
                    infoDescription.setVisibility(View.INVISIBLE);
                }
            }
        }catch (RuntimeException ignored){}
    }
}
