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
import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.activities.BaseActivity;
import it.unipi.sam.volleyballmovementtracker.activities.player.PlayerActivity;
import it.unipi.sam.volleyballmovementtracker.activities.trainer.TrainerActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMainBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import pub.devrel.easypermissions.EasyPermissions;


// TODO:
//  1. porta vectors xml ad al massimo 200x200
// 2. scritte davanti a scelta rompono il cazzo
public class MainActivity extends BaseActivity implements View.OnClickListener, DialogInterface.OnClickListener, EasyPermissions.PermissionCallbacks {
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
            // device doesn't support bt
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
                }else
                    bta.enable();
            }else
                bta.enable();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == binding.trainerView.getId()){
            // start trainer activity

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.coachIv, "who_am_i_choice");
            // start the new activity
            Intent i = new Intent(this, TrainerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_coach1);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
        }else if(view.getId() == binding.playerView.getId()){
            // start player activity

            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="who_am_i_choice"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, binding.coachIv, "who_am_i_choice");
            // start the new activity
            Intent i = new Intent(this, PlayerActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(Constants.who_am_i_id_key, R.drawable.ic_block2);
            i.putExtra(Constants.starting_activity_bundle_key, mBundle);
            startActivity(i, options.toBundle());
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