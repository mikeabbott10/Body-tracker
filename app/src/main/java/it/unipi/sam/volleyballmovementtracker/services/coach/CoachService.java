package it.unipi.sam.volleyballmovementtracker.services.coach;

import it.unipi.sam.volleyballmovementtracker.services.BluetoothService;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.GetBTConnectionsRunnable;

public class CoachService extends BluetoothService {
    /**
     * Called from bound coach activity after bt got discoverable
     */
    public void onMeDiscoverable() {
        updateBTState(Constants.BT_STATE_DISCOVERABLE);
        // get this listening for incoming connections
        getBTConnectionsRunnable =
                new GetBTConnectionsRunnable(this, bta, this);
        new Thread(getBTConnectionsRunnable).start();
    }
}
