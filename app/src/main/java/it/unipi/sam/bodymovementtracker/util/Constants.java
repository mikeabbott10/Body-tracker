package it.unipi.sam.bodymovementtracker.util;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constants {
    // bluetooth
    public static final int BT_ENABLE_PERMISSION_CODE = 0;
    public static final int BT_START_DISCOVER_PERMISSION_CODE = 1;
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static final String[] BT_PERMISSIONS =
            new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    // Constants that indicate the current bt state
    public static final int NULL_STATE = -1;
    public static final int BT_STATE_DISABLED = 0;      // bt is off
    public static final int BT_STATE_ENABLED = 1;       // bt is on but we're doing nothing
    public static final int BT_STATE_DISCOVERABLE_AND_LISTENING = 2;  // bt in discoverable state
    public static final int BT_STATE_DISCOVERING = 3;  // bt in discovering state
    public static final int BT_STATE_LISTEN = 4;        // now listening for incoming connections
    public static final int BT_STATE_CONNECTING = 5;    // now initiating an outgoing connection
    public static final int BT_STATE_CONNECTION_FAILED = 6; // tried to connect but failed
    public static final int BT_STATE_CONNECTED = 7;     // now connected to a remote device
    public static final int BT_STATE_JUST_DISCONNECTED = 8; // just disconnected from remote device
    public static final int BT_STATE_PERMISSION_REQUIRED = 9;     // permissions required
    public static final int BT_STATE_BADLY_DENIED = 10;  // permissions have been denied manually during service life (close the service)
    public static final int BT_STATE_UNSOLVED = 11;      // bt unknown error (close the service)

    // Constants that indicate the current service state
    public static final int CLOSING_SERVICE = 0;
    public static final int STARTING_SERVICE = 1;
    public static final int ALREADY_STARTED_SERVICE = 2;

    // bt constants
    public static final String BT_NAME = "Coach";
    public static final String BT_UUID = "be9067f2-c2d5-47a7-be41-8a36a7841cd1";

    // coach fragments
    public static final int COACH_STARTING_FRAGMENT = 0;
    public static final int COACH_PRACTICING_FRAGMENT = 1;

    // player fragments
    public static final int PLAYER_STARTING_FRAGMENT = 0;
    public static final int PLAYER_PRACTICING_FRAGMENT = 1;

    // roles
    public static final int COACH_CHOICE = 0;
    public static final int PLAYER_CHOICE = 1;

    // actions
    public static final int ACTION_START_TRAINING = 0;
    public static final int ACTION_STOP_TRAINING = 1;
    public static final int ACTION_START_DISCOVERING = 2;
    public static final int ACTION_GO_TO_PRACTICING_FRAGMENT = 3;

    // dialogs
    public static final int DISCOVERABILITY_DIALOG = 0;
    public static final int WORK_IN_PROGRESS_DIALOG = 1;
    public static final int BT_ENABLING_DIALOG = 2;
    public static final int BT_ENABLE_PERMISSIONS_DIALOG = 3;
    public static final int BT_START_DISCOVERY_PERMISSIONS_DIALOG = 4;
    public static final int BT_PERMANENTLY_DENIED_PERMISSIONS_DIALOG = 5;
    public static final int NOTIFICATION_ENABLING_DIALOG = 6;
    public static final int NOTIFICATION_CHANNEL_ENABLING_DIALOG = 7;
    public static final int CONNECTION_CLOSED_DIALOG = 8;

    // colors
    public static final int BLUE = 0;
    public static final int RED = 1;

    // drawables
    public static final int MB_BLOCK_IMAGE_ID = 0;
    public static final int MBH_BLOCK_IMAGE_ID = 1;

    // keys
    public static final String who_am_i_id_key = "wamiik";
    public static final String timestamp_key = "timestamp";
    public static final String id_key = "_id";
    public static final String starting_component_bundle_key = "sabk";
    public static final String current_training_id_key = "ctik";
    public static final String current_video_player_id_key = "cvpik";
    public static final String current_bt_state_id_key = "cbtsik";
    public static final String show_gesture_key = "sgk";
    public static final String scan_mode_status_key = "sssk";
    public static final String showing_dialog_key = "sdk";
    public static final String play_this_video_key = "ptvk";
    public static final String video_current_position_key = "vcpk";
    public static final String choice_key = "ck";
    public static final String should_unbind_key = "suk";
    public static final String req_discoverability_launched_key = "rdlk";
    public static final String req_bt_launched_key = "rblk";
    public static final String previous_graph_data_key = "pgdk";

    // net
    public static final String restBasePath = "https://donow.cloud/trackerapp/";
    public static final String firstRestReqPath = "restInfo.json";
    public static final String rest_info_instance_key = "riik";
    public static final int TIMESTAMP = 0;
    public static final int PATH = 1;
    public static final String trainings_rest_key = "trainings";
    public static final String video_file_name = "video.mp4";

    // ConnectedThread message
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int STREAM_DISCONNECTED = 3;

    // notification
    public static final String NOTIFICATION_CHANNEL_ID = "tracker_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Tracker channel";

    // db
    public static String DATABASE_NAME = "tracker_app_db";
}
