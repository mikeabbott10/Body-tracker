package it.unipi.sam.volleyballmovementtracker.util.db;

import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;

public class PerformActionOnDataTableRunnable implements Runnable {
    public static final int INSERT = 0;
    public static final int DELETE_BEFORE = 1;
    public static final int DELETE_ALL = 2;

    private final AppDatabase mDb;
    private DataWrapper dataWrapper;
    private int action;
    private long delete_before_this_timestamp;

    public PerformActionOnDataTableRunnable(AppDatabase db, DataWrapper dataWrapper, int action, long delete_before_this_timestamp) {
        this.mDb = db;
        this.dataWrapper = dataWrapper;
        this.action = action;
        this.delete_before_this_timestamp = delete_before_this_timestamp;
    }

    @Override
    public void run() {
        switch(action){
            case INSERT:{
                mDb.dataDAO().insertAll(dataWrapper);
                break;
            }
            case DELETE_BEFORE:{
                mDb.dataDAO().deleteBefore(delete_before_this_timestamp);
                break;
            }
            case DELETE_ALL:{
                mDb.dataDAO().deleteAll();
                break;
            }
        }
    }
}