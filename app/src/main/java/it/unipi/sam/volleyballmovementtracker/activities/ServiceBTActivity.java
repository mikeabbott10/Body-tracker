package it.unipi.sam.volleyballmovementtracker.activities;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;

public class ServiceBTActivity extends ServiceCommunicationActivity implements Observer<Integer>{
    protected BluetoothService mBoundService;
    public BluetoothService getmBoundService() {
        return mBoundService;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = ((BluetoothService.LocalBinder)service).getService();
                mBoundService.getBt_live_state().observe(ServiceBTActivity.this, ServiceBTActivity.this);
            }
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
            }
        };
    }

    @Override
    public void onChanged(Integer bt_state) {
        // bt state changed
        switch(bt_state){
            case Constants.BT_STATE_DISABLED:
            case Constants.BT_STATE_BADLY_DENIED:
            case Constants.BT_STATE_UNSOLVED: {
                updateBtIcon(binding.bluetoothState, R.drawable.disabled_bt,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
            case Constants.BT_STATE_ENABLED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, ResourcesCompat.ID_NULL, false);
                break;
            }
            case Constants.BT_STATE_DISCOVERABLE:
            case Constants.BT_STATE_LISTEN:
            case Constants.BT_STATE_CONNECTING: {
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.waiting, true);
                break;
            }
            case Constants.BT_STATE_CONNECTED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.ic_disabled_ok, false);
                break;
            }
            case Constants.BT_STATE_PERMISSION_REQUIRED:{
                updateBtIcon(binding.bluetoothState, R.drawable.ic_bluetooth1,
                        binding.bluetoothStateOverlay, R.drawable.ic_wrong, false);
                break;
            }
        }
    }
}
