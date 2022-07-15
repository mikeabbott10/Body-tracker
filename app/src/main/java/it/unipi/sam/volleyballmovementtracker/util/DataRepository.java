package it.unipi.sam.volleyballmovementtracker.util;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.util.db.AppDatabase;
import it.unipi.sam.volleyballmovementtracker.util.db.DataDAO;
import it.unipi.sam.volleyballmovementtracker.util.db.InsertDataRunnable;

public class DataRepository {
    private final AppDatabase db;
    private DataDAO mDataDao;
    private LiveData<List<DataWrapper>> mAllWords;

    public DataRepository(Application application) {
        db = AppDatabase.getDatabase(application);
        mDataDao = db.dataDAO();
        mAllWords = mDataDao.getAll();
    }

    public LiveData<List<DataWrapper>> getAllData() {
        return mAllWords;
    }

    public void insert (DataWrapper data) {
        new Thread(new InsertDataRunnable(db, data)).start();
    }
}