package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;

public class MyAlphaAnimation extends AlphaAnimation {
    private Object obj;
    public MyAlphaAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAlphaAnimation(float fromAlpha, float toAlpha) {
        super(fromAlpha, toAlpha);
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
