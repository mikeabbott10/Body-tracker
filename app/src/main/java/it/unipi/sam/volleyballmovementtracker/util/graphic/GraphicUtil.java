package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

public class GraphicUtil {
    // slide the view from its current position to above itself
    public static void slideUp(View view, String key, @Nullable Animation.AnimationListener anListener){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -view.getHeight());                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public static void slideDown(View view, @Nullable String key, @Nullable Animation.AnimationListener anListener){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from down to its position
    public static void slideUpToOrigin(View view, String key, @Nullable Animation.AnimationListener anListener){
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from up to its position
    public static void slideDownToOrigin(View view, String key, @Nullable Animation.AnimationListener anListener){
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from its  current position to left
    public static void slideLeft(@Nullable View view, String key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        view.setVisibility(View.VISIBLE);
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                - view.getWidth(),           // toXDelta
                0,              // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from its  current position to right
    public static void slideRight(@Nullable View view, String key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                0,                 // fromXDelta
                view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from right to its position
    public static void slideLeftToOrigin(@Nullable View view, String key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }

    // slide the view from left to its position
    public static void slideRightToOrigin(@Nullable View view, String key, @Nullable Animation.AnimationListener anListener) {
        if(view == null) return;
        MyTranslateAnimation animate = new MyTranslateAnimation(
                view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0);                 // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setObj(key);
        if(anListener!=null)
            animate.setAnimationListener(anListener);
        view.startAnimation(animate);
    }
}
