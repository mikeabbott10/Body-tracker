package it.unipi.sam.volleyballmovementtracker.services.player;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.ConnectToBTServerRunnable;

public class PlayerService extends BluetoothService {
    private List<BluetoothDevice> currentFoundDevicesList;

    public void saveFoundDevicesList(List<BluetoothDevice> list){
        currentFoundDevicesList = list;
    }
    public List<BluetoothDevice> retriveFoundDeviceList(){
        return currentFoundDevicesList;
    }

    /**
     * Called from bound player activity after user manually selected the device of the coach
     * @param btDevice the device we are gonna connect to
     */
    public void onBTServerSelected(BluetoothDevice btDevice){
        // get this listening for incoming connections
        connectToBTServerRunnable =
                new ConnectToBTServerRunnable(this, btDevice, bta, this);
        new Thread(connectToBTServerRunnable).start();
    }
}
