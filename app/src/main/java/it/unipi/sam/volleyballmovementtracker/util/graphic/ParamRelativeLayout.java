package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class ParamRelativeLayout extends RelativeLayout {
    private Object obj;

    public ParamRelativeLayout(Context context) {
        super(context);
    }

    public ParamRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ParamRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ParamRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setObject(Object object) {
        this.obj = object;
    }
    public Object getObj() {
        return obj;
    }
}