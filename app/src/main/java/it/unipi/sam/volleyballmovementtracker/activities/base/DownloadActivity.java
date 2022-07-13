package it.unipi.sam.volleyballmovementtracker.activities.base;

import android.app.DownloadManager;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.download.DMRequestWrapper;
import it.unipi.sam.volleyballmovementtracker.util.download.JacksonUtil;
import it.unipi.sam.volleyballmovementtracker.util.download.OnBroadcastReceiverOnDMReceiveListener;
import it.unipi.sam.volleyballmovementtracker.util.download.RestInfo;

public abstract class DownloadActivity extends DialogActivity implements OnBroadcastReceiverOnDMReceiveListener {
    private static final String TAG = "AAADownloadActivity";
    protected IntentFilter myIntentFilter;

    //overall
    protected static final int REST_INFO_JSON = 0;
    //trainings
    protected static final int TRAININGS_JSON = 2;

    protected DownloadManager dm;
    protected ConcurrentHashMap<Long, Integer> idToResourceType;

    protected RestInfo restInfoInstance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null) {
            restInfoInstance = savedInstanceState.getParcelable(Constants.rest_info_instance_key);
        }

        idToResourceType = new ConcurrentHashMap<>(); // fill it with ids of requests (takes count of resources which are requested from this context)
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // Register for broadcasts on BluetoothAdapter state and scan mode change
        myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    }

    protected void getRestInfoFileFromNet(DMRequestWrapper dmRequestWrapper) {
        enqueueRequest(dmRequestWrapper);
    }

    protected void getTrainingsInfoFileFromNet(DMRequestWrapper dmRequestWrapper) {
        enqueueRequest(dmRequestWrapper);
    }

    private void enqueueRequest(DMRequestWrapper dmReq){
        long id = dm.enqueue(dmReq.getReq()); // long id
        idToResourceType.put(id, dmReq.getResourceType());
    }

    @Override
    public void onDownloadCompleted(long id) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        Cursor c = dm.query(q);
        if (c.moveToFirst()) {
            int j = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int h = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            int t = c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP); // note: if it fails returns -1.
            int status = c.getInt(j);
            int columnReason = c.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = c.getInt(columnReason);
            String uriString = c.getString(h);
            Integer resources_type = idToResourceType.get(id);
            if(status == DownloadManager.STATUS_SUCCESSFUL){
                long lastModifiedTimestamp = c.getLong(t);
                if(resources_type==null) {
                    c.close();
                    return;
                }
                //Log.d( TAG, "onDownloadCompleted. "+resources_type+" ready: "+ uriString);
                handleResponseUri(id, resources_type, uriString, lastModifiedTimestamp, true);
                if(resources_type==REST_INFO_JSON) // rest info file content is in memory now and we don't want to keep the file
                    dm.remove(id); // delete last downloaded restInfo record from dm
            }else if(status == DownloadManager.STATUS_FAILED) {
                Log.d( TAG, "download failed reason: " + reason);
                handle404(id, resources_type, uriString);
            }else if(status == DownloadManager.STATUS_PAUSED) {
                Log.d( TAG, "download paused reason: " + reason);
            }else if(status == DownloadManager.STATUS_PENDING) {
                Log.d( TAG, "download status: PENDING");
            }else if(status == DownloadManager.STATUS_RUNNING){
                Log.d( TAG, "download status: RUNNING");
            }
        }
        c.close();
    }

    /**
     * Handle uri.
     * @param type type of the resource
     * @param uriString local uri as string
     * @param lastModifiedTimestamp last modified timestamp
     * @param updateResourcePreference set it to true if you are handling a just downloaded resource
     */
    // override this in activities (call super.handleResponseUri(...))
    protected void handleResponseUri(long dm_resource_id, Integer type, String uriString,
                                     long lastModifiedTimestamp, boolean updateResourcePreference) {
        if (type == REST_INFO_JSON) {
            // here after sending the first request. We got the rest service info.

            // get the file content
            String content = getFileContentFromUri(uriString);

            // perform jackson from file to object
            try {
                restInfoInstance =
                        (RestInfo) JacksonUtil.getObjectFromString(content, RestInfo.class);
            } catch (JsonProcessingException e) {
                Log.e(TAG, "", e);
            }
        }
    }

    protected String getFileContentFromUri(String uriString) {
        InputStream is;
        try {
            is = getContentResolver().openInputStream(Uri.parse(uriString));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "", e);
            return null;
        }

        StringBuilder content;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is))){
            String line;
            content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
            try {
                is.close();
            } catch (IOException ignored) {}
            return null;
        }

        try {
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "", e);
            return null;
        }
        return content.toString();
    }

    // abstract
    protected abstract void handle404(long dm_resource_id, Integer type, String uriString);
}

