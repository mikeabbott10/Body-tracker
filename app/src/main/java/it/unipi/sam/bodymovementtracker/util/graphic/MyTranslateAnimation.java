package it.unipi.sam.bodymovementtracker.util.graphic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;

public class MyTranslateAnimation extends TranslateAnimation {
    private Object obj;

    public MyTranslateAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
    }

    public MyTranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
