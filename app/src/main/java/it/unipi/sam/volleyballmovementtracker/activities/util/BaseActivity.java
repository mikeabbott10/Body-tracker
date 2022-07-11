package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import pub.devrel.easypermissions.EasyPermissions;

public class BaseActivity extends DownloadActivity implements
        EasyPermissions.PermissionCallbacks, ActivityResultCallback<ActivityResult> {
    private static final String TAG = "AAAABaseActivity";
    protected BluetoothAdapter bta;
    public static final int extraDiscoverableDuration = 240;

    protected ActivityResultLauncher<Intent> requestDiscoverabilityLauncher;
    private ActivityResultLauncher<Intent> requestBluetoothLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestDiscoverabilityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this);

        requestBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //granted
                        if (!bta.isEnabled())
                            enableBT();
                    } else {
                        //deny
                        //finishAffinity();
                    }
                });

        bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch(requestCode){
            case Constants.BT_ENABLE_PERMISSION_CODE: {
                // bt permissions granted
                if (!bta.isEnabled()) {
                    enableBT();
                }
                break;
            }
            case Constants.BT_START_DISCOVER_PERMISSION_CODE:{
                bta.startDiscovery();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            showMyDialog(Constants.BT_PERMANENTLY_DENIED_PERMISSIONS_DIALOG);
            return;
        }
        switch(requestCode) {
            case Constants.BT_ENABLE_PERMISSION_CODE: {
                showMyDialog(Constants.BT_ENABLE_PERMISSIONS_DIALOG);
                break;
            }
            case Constants.BT_START_DISCOVER_PERMISSION_CODE: {
                showMyDialog(Constants.BT_START_DISCOVERY_PERMISSIONS_DIALOG);
                break;
            }
        }
    }


    @Override
    public void onActivityResult(ActivityResult result) {
        //Log.i(TAG, "onActivityResult: " + result.getResultCode() );
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        super.onClick(dialogInterface, i);
        if(showingDialog == Constants.BT_ENABLING_DIALOG){
            if(i==AlertDialog.BUTTON_POSITIVE) {
                askForEnablingBt();
            }//else finishAffinity();
            return;
        }
        if(showingDialog == Constants.BT_ENABLE_PERMISSIONS_DIALOG){
            if(i== AlertDialog.BUTTON_POSITIVE) {
                askForEnablingBt();
            }//else finishAffinity();
            return;
        }
        if(showingDialog == Constants.BT_PERMANENTLY_DENIED_PERMISSIONS_DIALOG){
            //finishAffinity();
        }
    }

    // utils --------------------------------------------------------
    protected void askForEnablingBt() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // check bt permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request),
                        Constants.BT_ENABLE_PERMISSION_CODE, Constants.BT_PERMISSIONS);
            } else {
                if(!bta.isEnabled()) {
                    requestBluetoothLauncher.launch(enableBtIntent);
                }
            }
        }else{
            if(!bta.isEnabled()) {
                requestBluetoothLauncher.launch(enableBtIntent);
            }
        }
    }

    protected void askForDiscoverability() {// check bt permissions
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, extraDiscoverableDuration);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request),
                        Constants.BT_ENABLE_PERMISSION_CODE, Constants.BT_PERMISSIONS);
            } else {
                if(!bta.isEnabled()) {
                    requestDiscoverabilityLauncher.launch(i);
                }
            }
        }else{
            if(!bta.isEnabled()) {
                requestDiscoverabilityLauncher.launch(i);
            }
        }
    }

    private void enableBT() {
        try {
            bta.enable();
        }catch(SecurityException e){
            Log.e(TAG, "No permissions (Should never reach this)", e);
        }
    }
}
