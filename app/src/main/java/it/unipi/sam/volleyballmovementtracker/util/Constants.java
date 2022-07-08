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

    // coach fragments
    public static final int SELECT_TRAINING_FRAGMENT = 0;
    public static final int GET_CONNECTIONS_FRAGMENT = 1;
    public static final int PICKER_FRAGMENT = 2;
    public static final int GET_DATA_FRAGMENT = 3;
    public static final int SHOW_DATA_FRAGMENT = 4;

    // player fragments
    public static final int PLAYER_STARTING_FRAGMENT = 0;
    public static final int PLAYER_PRACTICING_FRAGMENT = 1;
    public static final int VIDEO_PLAYER_PLAYLIST_FRAGMENT = 2;

    // actions
    public static final int COACH_CHOICE = 0;
    public static final int PLAYER_CHOICE = 1;
    public static final int STARTING_MB_TRAINING = 2;
    public static final int STARTING_MB_HITTER_TRAINING = 3;
    //coach
    public static final int GO_TO_START_CONNECTION_FRAGMENT = 4;
    public static final int BACK_TO_INIT_FRAGMENT = 5;

    // dialogs
    public static final int DISCOVERABILITY_DIALOG = 0;
    public static final int WORK_IN_PROGRESS_DIALOG = 1;
    public static final int BT_ENABLING_DIALOG = 2;

    // videos
    public static final int MIDDLE_BLOCK_VIDEO = 0;
    public static final String MIDDLE_BLOCK_VIDEO_URL = "http://techslides.com/demos/sample-videos/small.mp4";
    public static final int SLIDE_MIDDLE_BLOCK_VIDEO = 1;
    public static final String SLIDE_MIDDLE_BLOCK_VIDEO_URL = "http://techslides.com/demos/sample-videos/small.mp4";

    // colors
    public static final int BLUE = 0;
    public static final int RED = 1;

    // drawables
    public static final int MB_BLOCK_IMAGE_ID = 0;
    public static final int MBH_BLOCK_IMAGE_ID = 1;

    // keys
    public static final String who_am_i_id_key = "wamiik";
    public static String timestamp_key = "timestamp";
    public static String id_key = "_id";
    public static final String starting_component_bundle_key = "sabk";
    public static final String current_training_id_key = "ctik";
    public static final String current_video_player_id_key = "cvpik";
    public static final String current_bt_state_id_key = "cbtsik";
    public static final String show_gesture_key = "sgk";
    public static final String scan_mode_status_key = "sssk";
    public static final String showing_dialog_key = "sdk";
    public static final String play_this_video_key = "ptvk";
    public static final String choice_key = "ck";

    // net
    public static String restBasePath = "https://donow.cloud/trackerapp/";
    public static String firstRestReqPath = "restInfo.json";
    public static String rest_info_instance_key = "riik";
    public static final int TIMESTAMP = 0;
    public static final int PATH = 1;
    public static String trainings_rest_key = "trainings";
}
