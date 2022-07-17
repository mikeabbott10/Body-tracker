package it.unipi.sam.bodymovementtracker.util;

import static it.unipi.sam.bodymovementtracker.util.db.PerformActionOnDataTableRunnable.INSERT;
import static it.unipi.sam.bodymovementtracker.util.db.PerformActionOnDataTableRunnable.DELETE_ALL;
import static it.unipi.sam.bodymovementtracker.util.db.PerformActionOnDataTableRunnable.DELETE_BEFORE;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.unipi.sam.bodymovementtracker.util.db.AppDatabase;
import it.unipi.sam.bodymovementtracker.util.db.DataDAO;
import it.unipi.sam.bodymovementtracker.util.db.PerformActionOnDataTableRunnable;

public class DataRepository {
    private final AppDatabase db;
    private final LiveData<List<DataWrapper>> mLastData;
    private DataDAO mDataDao;
    private LiveData<List<DataWrapper>> mAllData;

    public DataRepository(Application application) {
        db = AppDatabase.getDatabase(application);
        mDataDao = db.dataDAO();
        mAllData = mDataDao.getAll();
        mLastData = mDataDao.getLast();
    }

    public LiveData<List<DataWrapper>> getAllData() {
        return mAllData;
    }
    public LiveData<List<DataWrapper>> getLastData() {
        return mLastData;
    }

    public void insert (DataWrapper data) {
        new Thread(new PerformActionOnDataTableRunnable(db, data, INSERT, 0)).start();
    }
    public void deleteBefore (long timestamp) {
        new Thread(new PerformActionOnDataTableRunnable(db, null, DELETE_BEFORE, timestamp)).start();
    }
    public void deleteAll () {
        new Thread(new PerformActionOnDataTableRunnable(db, null, DELETE_ALL, 0)).start();
    }
}