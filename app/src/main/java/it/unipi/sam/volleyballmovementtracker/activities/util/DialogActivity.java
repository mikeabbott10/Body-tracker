package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class DialogActivity extends GUIBaseActivity implements DialogInterface.OnClickListener{
    protected int showingDialog;
    protected AlertDialog workInProgressDialog;
    protected AlertDialog discoverabilityDeniedDialog;
    protected AlertDialog btEnablingDialog;
    protected AlertDialog btPermissionDeniedDialog;
    protected AlertDialog btPermissionPermanentlyDeniedDialog;
    protected AlertDialog btStartDiscoveryPermissionDeniedDialog;
    protected AlertDialog notificationEnablingDialog;
    protected AlertDialog notificationChannelEnablingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showingDialog = -1;
        // get original instance state
        if(savedInstanceState!=null){
            showingDialog = savedInstanceState.getInt(Constants.showing_dialog_key);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.showing_dialog_key, showingDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        switch (showingDialog) {
            case Constants.BT_ENABLING_DIALOG:
                try{ btEnablingDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.DISCOVERABILITY_DIALOG:
                try{ discoverabilityDeniedDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.WORK_IN_PROGRESS_DIALOG:
                try{ workInProgressDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.BT_ENABLE_PERMISSIONS_DIALOG:
                try{ btPermissionDeniedDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.BT_PERMANENTLY_DENIED_PERMISSIONS_DIALOG:
                try{ btPermissionPermanentlyDeniedDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.BT_START_DISCOVERY_PERMISSIONS_DIALOG:
                try{ btStartDiscoveryPermissionDeniedDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.NOTIFICATION_ENABLING_DIALOG:
                try{ notificationEnablingDialog.cancel(); }catch (RuntimeException ignored){}
                break;
            case Constants.NOTIFICATION_CHANNEL_ENABLING_DIALOG:
                try{ notificationChannelEnablingDialog.cancel(); }catch (RuntimeException ignored){}
                break;
        }
    }

    // dialog
    protected void initDialog() {
        btEnablingDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.bt_not_enabled))
                .setMessage(getString(R.string.enable_bt))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), this)
                .create();

        workInProgressDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.work_in_progress_warning_title))
                .setMessage(getString(R.string.work_in_progress_warning))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), null)
                .create();

        discoverabilityDeniedDialog = new MaterialAlertDialogBuilder(this)
                .setTitle( getString(R.string.bt_discoverability_not_enabled))
                .setMessage(getString(R.string.enable_discoverability))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), this)
                .create();

        btPermissionDeniedDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.bt_not_permitted))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), this)
                .create();

        btStartDiscoveryPermissionDeniedDialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.bt_not_permitted))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.accept), this)
                .setNegativeButton(getString(R.string.decline), this)
                .create();

        btPermissionPermanentlyDeniedDialog = new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.bt_permanently_not_permitted))
                .setCancelable(false)
                .setPositiveButton("OK", this)
                .create();

        notificationEnablingDialog = new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.ask_enable_notification))
                .setTitle(getString(R.string.alert_title))
                .setCancelable(false)
                .setPositiveButton("", this)
                .create();

        notificationChannelEnablingDialog = new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.ask_enable_channel_notification))
                .setTitle(getString(R.string.alert_title))
                .setCancelable(false)
                .setPositiveButton("",this)
                .create();
    }

    protected void showMyDialog(int dialogCode){
        showingDialog = dialogCode;
        switch (showingDialog) {
            case Constants.BT_ENABLING_DIALOG:
                btEnablingDialog.show();
                break;
            case Constants.DISCOVERABILITY_DIALOG:
                discoverabilityDeniedDialog.show();
                break;
            case Constants.WORK_IN_PROGRESS_DIALOG:
                workInProgressDialog.show();
                break;
            case Constants.BT_ENABLE_PERMISSIONS_DIALOG:
                btPermissionDeniedDialog.show();
                break;
            case Constants.BT_START_DISCOVERY_PERMISSIONS_DIALOG:
                btStartDiscoveryPermissionDeniedDialog.show();
                break;
            case Constants.BT_PERMANENTLY_DENIED_PERMISSIONS_DIALOG:
                btPermissionPermanentlyDeniedDialog.show();
                break;
            case Constants.NOTIFICATION_ENABLING_DIALOG:
                notificationEnablingDialog.show();
                break;
            case Constants.NOTIFICATION_CHANNEL_ENABLING_DIALOG:
                notificationChannelEnablingDialog.show();
                break;
        }
    }

    // override these
    protected void askForEnablingBt() {}
    @Override public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
    }
}
