package it.unipi.sam.volleyballmovementtracker.util;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constants {
    // bluetooth
    public static final int BT_PERMISSION_CODE = 0;
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static String[] BT_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    // bt constants
    public static String BT_NAME = "Coach";
    public static String BT_UUID = "be9067f2-c2d5-47a7-be41-8a36a7841cd1";

    // fragments
    public static final int PICKER_FRAGMENT = 0;
    public static final int GET_CONNECTIONS_FRAGMENT = 1;

    // actions
    public static final int STARTING_COACH_ACTIVITY = 0;
    public static final int STARTING_PLAYER_ACTIVITY = 1;
    public static final int STARTING_MB_TRAINING = 2;
    public static final int STARTING_MB_HITTER_TRAINING = 3;
    public static final int FROM_PICKER_TO_NEXT_FRAGMENT = 4;
    public static final int BACK_TO_PICKER_FRAGMENT = 5;

    public static final int DISCOVERABILITY_DIALOG = 0;
    public static final int WORK_IN_PROGRESS_DIALOG = 1;
    public static final int BT_ENABLING_DIALOG = 2;

    public static String who_am_i_id_key = "wamiik";
    public static String starting_component_bundle_key = "sabk";
    public static String current_training_id_key = "ctik";
    public static String current_bt_state_id_key = "cbtsik";
    public static String show_gesture_key = "sgk";
    public static String scan_mode_status_key = "sssk";
    public static String showing_dialog_key = "sdk";
}
