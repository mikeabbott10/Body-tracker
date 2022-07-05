package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import it.unipi.sam.volleyballmovementtracker.R;

public class GraphicUtil {
    private static final long animationDuration = 70;

    // slide the view from its current position to above itself
    public static void slideUp(View view, int key, @Nullable Animation.AnimationListener anListener){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -view.getHeight());                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void slideDown(View view, int key, @Nullable Animation.AnimationListener anListener){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from down to its position
    public static void slideUpToOrigin(View view, int key, @Nullable Animation.AnimationListener anListener){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from up to its position
    public static void slideDownToOrigin(View view, int key, @Nullable Animation.AnimationListener anListener){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its  current position to left
    public static void slideLeft(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                - view.getWidth(),           // toXDelta
                0,              // fromYDelta
                0);                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its  current position to right
    public static void slideRight(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from right to its position
    public static void slideLeftToOrigin(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from left to its position
    public static void slideRightToOrigin(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }


    // slide the view from its current position to above itself
    public static void slideUp(View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -view.getHeight());                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void slideDown(View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from down to its position
    public static void slideUpToOrigin(View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from up to its position
    public static void slideDownToOrigin(View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its  current position to left
    public static void slideLeft(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration) {
        if(view == null) return;
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                - view.getWidth(),           // toXDelta
                0,              // fromYDelta
                0);                // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from its  current position to right
    public static void slideRight(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from right to its position
    public static void slideLeftToOrigin(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    // slide the view from left to its position
    public static void slideRightToOrigin(@Nullable View view, int key, @Nullable Animation.AnimationListener anListener, long animationDuration) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(animationDuration);
        animate.setFillAfter(true);
        if(anListener!=null){
            animate.setObj(key);
            animate.setAnimationListener(anListener);
        }
        view.startAnimation(animate);
    }

    public static void scaleImage(Context ctx, @Nullable View view, int key, @Nullable Animation.AnimationListener anListener){
        if(view == null)
            return;
        MyAnimation animation = new MyAnimation(AnimationUtils.loadAnimation(ctx, R.anim.scale));
        if(anListener!=null) {
            animation.setObj(key);
            animation.getAnimation().setAnimationListener(anListener);
        }
        view.startAnimation(animation.getAnimation());
    }
}
