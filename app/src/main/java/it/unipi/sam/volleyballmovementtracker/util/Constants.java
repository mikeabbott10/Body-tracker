package it.unipi.sam.volleyballmovementtracker.util;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constants {
    // bluetooth
    public static final int BT_PERMISSION_CODE = 0;
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static String[] BT_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    // fragments
    public static final int PICKER_FRAGMENT = 0;
    public static final int GET_CONNECTIONS_FRAGMENT = 1;

    public static final int STARTING_COACH_ACTIVITY = 0;
    public static final int STARTING_PLAYER_ACTIVITY = 1;
    public static final int STARTING_MB_TRAINING = 2;
    public static final int STARTING_MB_HITTER_TRAINING = 3;
    public static final int FROM_PICKER_TO_NEXT_FRAGMENT = 4;
    public static final int BACK_TO_PICKER_FRAGMENT = 5;

    public static String who_am_i_id_key = "wamiik";
    public static String starting_component_bundle_key = "sabk";
    public static String current_training_id_key = "ctik";
    public static String current_bt_state_id_key = "cbtsik";
    public static String show_gesture_key = "sgk";
    public static String asked_for_discoverability_key = "afdk";
}
