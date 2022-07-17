package it.unipi.sam.bodymovementtracker.util.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unipi.sam.bodymovementtracker.util.DataRepository;
import it.unipi.sam.bodymovementtracker.util.DataWrapper;

public class DataViewModel extends AndroidViewModel {
    private final LiveData<List<DataWrapper>> mLastData;
    private DataRepository mRepository;
    private LiveData<List<DataWrapper>> mAllData;

    public DataViewModel (Application application) {
        super(application);
        mRepository = new DataRepository(application);
        mAllData = mRepository.getAllData();
        mLastData = mRepository.getLastData();
    }

    public LiveData<List<DataWrapper>> getAllData() { return mAllData; }
    public LiveData<List<DataWrapper>> getLastData() { return mLastData; }
    public void insert(DataWrapper data) { mRepository.insert(data); }
    public void deleteBefore(long timestamp) { mRepository.deleteBefore(timestamp); }
    public void deleteAll() { mRepository.deleteAll(); }
}