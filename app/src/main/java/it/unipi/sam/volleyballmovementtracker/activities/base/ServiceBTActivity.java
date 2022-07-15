package it.unipi.sam.volleyballmovementtracker.activities.base;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import it.unipi.sam.volleyballmovementtracker.services.SensorService;

public abstract class ServiceBTActivity extends ServiceCommunicationActivity{
    private static final String TAG = "AAAServicBTActivit";
    public SensorService mBoundService;

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
                Log.d(TAG, "onServiceConnected");
                mBoundService = ((SensorService.LocalBinder)service).getService();
                if(mBoundService.isStarted){
                    Log.d(TAG, "isStarted!");
                    mBoundService.getLive_service_state().observe(ServiceBTActivity.this,
                            serviceState -> handleServiceStateChange(serviceState));
                    mBoundService.getLive_bt_state().observe(ServiceBTActivity.this,
                            btState -> handleServiceBTStateChange(btState));
                    mBoundService.getLive_message_data().observe(ServiceBTActivity.this,
                            messageWrapper -> {
                                if(messageWrapper!=null) {
                                    handleReceivedDataFromMessage(messageWrapper.msg);
                                }
                            });
                    onServiceAlreadyStarted(); // check role consistency
                    return;
                }
                doUnbindService();
                mBoundService = null;
            }
            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null;
                mShouldUnbind = false;
            }
        };
    }
    protected abstract void handleReceivedDataFromMessage(Message msg);
    protected abstract void handleServiceStateChange(int serviceState);
    protected abstract void handleServiceBTStateChange(int btState);
    protected abstract void onServiceAlreadyStarted();

    @Override
    protected void onResume() {
        super.onResume();
        doBindService(SensorService.class);
    }


    @Override
    protected void doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            try{
                mBoundService.getLive_bt_state().removeObservers(this);
                mBoundService.getLive_service_state().removeObservers(this);
                mBoundService.getLive_message_data().removeObservers(this);
            }catch(Exception ignored){}
            super.doUnbindService();
        }
    }

    @Override
    protected void handleDeniedBTEnabling(){
        myStopService();
    }

    @Override
    protected void handleBTEnabled() {
        Log.d(TAG, "handleBTEnabled");
        if(mBoundService!=null)
            mBoundService.onMeEnabled();
    }

    protected void myStopService(){
        if(mBoundService!=null)
            mBoundService.myStop();
        doUnbindService();
    }
}
