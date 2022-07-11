package it.unipi.sam.volleyballmovementtracker.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.unipi.sam.volleyballmovementtracker.util.download.OnBroadcastReceiverOnDMReceiveListener;

public class MyDMBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CLCLMyDMBroadcReceiv";
    private final OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener;

    public MyDMBroadcastReceiver(OnBroadcastReceiverOnDMReceiveListener onDMReceiveListener){
        this.onDMReceiveListener = onDMReceiveListener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            // qui gestiamo il completamento del download
            onDMReceiveListener.onDownloadCompleted(id);
        }
    }
}
