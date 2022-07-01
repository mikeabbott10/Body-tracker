package it.unipi.sam.volleyballmovementtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.activities.player.PlayerActivity;
import it.unipi.sam.volleyballmovementtracker.activities.trainer.TrainerActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.volleyballmovementtracker.util.graphic.Constants;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "AAAMainActivity";
    private ActivityMainBinding binding;
    private BluetoothAdapter bta;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.trainerView.setOnClickListener(this);
        binding.playerView.setOnClickListener(this);

        // check bt permissions
        bta = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(bta==null){
            // doesn't support bt
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.bt_not_supported))
                    .setCancelable(false)
                    .setPositiveButton("", this)
                    .setPositiveButtonIcon(AppCompatResources.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel))
                    .create().show();
            return;
        }

        if (!bta.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!EasyPermissions.hasPermissions(this, Constants.BT_PERMISSIONS)) {
                    EasyPermissions.requestPermissions(this, getString(R.string.bt_permissions_request), Constants.BT_PERMISSION_CODE, Constants.BT_PERMISSIONS);
                }
            }else
                bta.enable();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == binding.trainerView.getId()){
            // start trainer activity
            startActivity(new Intent(this, TrainerActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }else if(view.getId() == binding.playerView.getId()){
            // start player activity
            startActivity(new Intent(this, PlayerActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        finish();
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
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.bt_not_permitted))
                .setCancelable(false)
                .setPositiveButton("", this)
                .setPositiveButtonIcon(AppCompatResources.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel))
                .create().show();
    }
}