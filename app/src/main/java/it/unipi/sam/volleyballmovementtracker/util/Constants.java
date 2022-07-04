package it.unipi.sam.volleyballmovementtracker.util;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constants {
    // bluetooth
    public static final int BT_PERMISSION_CODE = 0;
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static String[] BT_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    public static final int PICKER_FRAGMENT = 0;

    public static final String STARTING_COACH_ACTIVITY = "sca";
    public static final String STARTING_PLAYER_ACTIVITY = "spa";
    public static final String STARTING_MB_TRAINING = "smba";
    public static final String STARTING_MB_HITTER_TRAINING = "smbha";

    public static String who_am_i_id_key = "wamiik";
    public static String starting_activity_bundle_key = "sabk";
    public static String current_training_id_key = "ctik";
    public static String current_bt_state_id_key = "cbtsik";
}
