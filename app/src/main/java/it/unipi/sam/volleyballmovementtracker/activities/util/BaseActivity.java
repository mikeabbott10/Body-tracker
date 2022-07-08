package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.OnBroadcastReceiverOnBTReceiveListener;
import pub.devrel.easypermissions.EasyPermissions;

public class BaseActivity extends GUIBaseActivity implements OnBroadcastReceiverOnBTReceiveListener,
                EasyPermissions.PermissionCallbacks, ActivityResultCallback<ActivityResult> {
    private static final String TAG = "AAAABaseActivity";
    protected BluetoothAdapter bta;
    protected BroadcastReceiver mReceiver;
    protected IntentFilter myIntentFilter;

    protected MyViewModel viewModel;
    protected ActivityResultLauncher<Intent> activityResultLaunch;
    public static final int extraDiscoverableDuration = 240;
    protected int currentScanModeStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // init current scan status
        currentScanModeStatus = BluetoothAdapter.SCAN_MODE_NONE;

        // get original instance state
        if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
            currentScanModeStatus = savedInstanceState.getInt(Constants.scan_mode_status_key);
        }else {
            super.onCreate(null);
        }

        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this);

        // Register for broadcasts on BluetoothAdapter state and scan mode change
        myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);// init

        bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if(showingDialog != Constants.BT_ENABLING_DIALOG)
            checkBTPermissionAndEnableBT();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.scan_mode_status_key, currentScanModeStatus);
        outState.putInt(Constants.showing_dialog_key, showingDialog);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void checkBTPermissionAndEnableBT() {
        // check bt permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request),
                        Constants.BT_PERMISSION_CODE, Constants.BT_PERMISSIONS);
            } else {
                bta.enable();
            }
        }else{
            bta.enable();
        }
        resetMyBluetoothStatus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast listener
        unregisterReceiver(mReceiver);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBluetoothStateChangedEventReceived(Context context, Intent intent) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                // bluetooth off
                // check bt permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                        EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request),
                                Constants.BT_PERMISSION_CODE, Constants.BT_PERMISSIONS);
                    }else {
                        showMyDialog(Constants.BT_ENABLING_DIALOG);
                    }
                }else{
                    showMyDialog(Constants.BT_ENABLING_DIALOG);
                }
                break;
            case BluetoothAdapter.STATE_ON:
                // bluetooth on
                showingDialog = -1;
                if(btEnablingDialog.isShowing()){
                    btEnablingDialog.cancel();
                }
                break;
        }
    }

    @Override
    public void onBluetoothScanModeChangedEventReceived(int scanMode) {
        //viewModel.selectScanModeStatus(scanMode);
        currentScanModeStatus = scanMode;
    }

    protected void resetMyBluetoothStatus(){
        // chiudere connessioni con tutti (non spegnere BT)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // bt permissions granted
        if (!bta.isEnabled()) {
            bta.enable();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.bt_not_permitted))
                .setCancelable(false)
                .setPositiveButton("OK", ((dialogInterface, i) -> finish()))
                .create().show();
    }

    protected void askForDiscoverability() {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, extraDiscoverableDuration);
        activityResultLaunch.launch(i);
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        //Log.i(TAG, "onActivityResult: " + result.getResultCode() );
    }
}
