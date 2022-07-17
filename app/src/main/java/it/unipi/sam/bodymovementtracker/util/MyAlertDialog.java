package it.unipi.sam.bodymovementtracker.util;

import androidx.appcompat.app.AlertDialog;

public class MyAlertDialog{
    private Object obj;
    private AlertDialog ad;

    public MyAlertDialog(AlertDialog alertDialog) {
        this.ad = alertDialog;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public AlertDialog getAd() {
        return ad;
    }

    public void setAd(AlertDialog ad) {
        this.ad = ad;
    }
}
