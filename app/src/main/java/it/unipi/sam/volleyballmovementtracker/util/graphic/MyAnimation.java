package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.view.animation.Animation;

public class MyAnimation{
    private Object obj;
    private Animation animation;

    public MyAnimation(Animation animation) {
        this.animation = animation;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }
}
