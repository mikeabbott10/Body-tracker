package it.unipi.sam.volleyballmovementtracker.activities.player;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import it.unipi.sam.volleyballmovementtracker.activities.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityTrainerBinding;
import it.unipi.sam.volleyballmovementtracker.util.MyBroadcastReceiver;
import it.unipi.sam.volleyballmovementtracker.util.OnBroadcastReceiverOnReceiveListener;

public class PlayerActivity extends SharedElementBaseActivity implements OnBroadcastReceiverOnReceiveListener {
    private static final String TAG = "AAAPlayerActivity";
    private ActivityTrainerBinding binding;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainerBinding.inflate(getLayoutInflater());
        assert binding.whoAmIIv != null;
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));

        setContentView(binding.getRoot());
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
    public void onPause() {
        super.onPause();

        // Unregister broadcast listener
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBluetoothEventReceived(Context context, Intent intent) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                // bluetooth off

                break;
            case BluetoothAdapter.STATE_ON:
                // bluetooth on

                break;
            //case BluetoothAdapter.STATE_TURNING_OFF:
            // turning Bluetooth off...
            //break;

            //case BluetoothAdapter.STATE_TURNING_ON:
            // turning Bluetooth on...
            //break;
        }
    }
}
