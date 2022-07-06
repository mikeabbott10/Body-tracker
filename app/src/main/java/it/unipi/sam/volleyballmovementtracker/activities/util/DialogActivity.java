package it.unipi.sam.volleyballmovementtracker.activities.util;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class DialogActivity extends AppCompatActivity implements DialogInterface.OnClickListener{
    protected int showingDialog;
    protected AlertDialog workInProgressDialog;
    protected AlertDialog discoverabilityDeniedDialog;
    protected AlertDialog btEnablingDialog;

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
        }
    }

    // override these
    protected void checkBTPermissionAndEnableBT() {}
    @Override public void onClick(DialogInterface dialogInterface, int i) {}
}
