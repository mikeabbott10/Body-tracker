package it.unipi.sam.bodymovementtracker.util.graphic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationSet;

public class MyAnimationSet extends AnimationSet {
    private Object obj;

    public MyAnimationSet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAnimationSet(boolean shareInterpolator) {
        super(shareInterpolator);
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

}
