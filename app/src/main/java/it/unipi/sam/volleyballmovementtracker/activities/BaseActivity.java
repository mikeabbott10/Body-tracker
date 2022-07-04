package it.unipi.sam.volleyballmovementtracker.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.OnBroadcastReceiverOnReceiveListener;
import pub.devrel.easypermissions.EasyPermissions;

public class BaseActivity extends AppCompatActivity implements OnBroadcastReceiverOnReceiveListener,
                DialogInterface.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "AAAABaseActivity";
    protected BluetoothAdapter bta;
    private BroadcastReceiver mReceiver;
    private AlertDialog btEnablingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // get custom instance state or original instance state
        Bundle startingThisActivityBundle = getIntent().getBundleExtra(Constants.starting_activity_bundle_key);
        if(startingThisActivityBundle != null){
            super.onCreate(savedInstanceState);
        }else if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
        }else {
            super.onCreate(null);
        }

        bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        btEnablingDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.bt_not_enabled))
                .setMessage(getString(R.string.enable_bt))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), ((dialogInterface, i) -> finish()))
                .create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mReceiver = new MyBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister broadcast listener
        unregisterReceiver(mReceiver);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBluetoothEventReceived(Context context, Intent intent) {
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
                        btEnablingDialog.show();
                    }
                }else{
                    btEnablingDialog.show();
                }
                break;
            case BluetoothAdapter.STATE_ON:
                // bluetooth on
                if(btEnablingDialog.isShowing()){
                    btEnablingDialog.hide();
                }
                break;
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // enable bt
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request), Constants.BT_PERMISSION_CODE, Constants.BT_PERMISSIONS);
            }else
                bta.enable();
        }else
            bta.enable();
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
}
