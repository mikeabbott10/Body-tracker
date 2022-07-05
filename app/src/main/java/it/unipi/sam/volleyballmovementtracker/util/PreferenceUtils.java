package it.unipi.sam.volleyballmovementtracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    private static final String TAG = "CLCLSharedPreference";

    // show gesture info
    public static boolean getShowGesture(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(Constants.show_gesture_key, true);
    }
    public static void setShowGesture(Context ctx, boolean show) {
        SharedPreferences.Editor spEditor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        spEditor.putBoolean(Constants.show_gesture_key, show);
        spEditor.apply();
    }
}
