package it.unipi.sam.volleyballmovementtracker.util.graphic;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class Constants {
    // bluetooth
    public static final int BT_PERMISSION_CODE = 0;
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static String[] BT_PERMISSIONS = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

}
