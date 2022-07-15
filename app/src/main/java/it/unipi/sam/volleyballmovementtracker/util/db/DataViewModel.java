package it.unipi.sam.volleyballmovementtracker.util.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.util.DataRepository;
import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;

public class DataViewModel extends AndroidViewModel {
    private DataRepository mRepository;
    private LiveData<List<DataWrapper>> mAllData;

    public DataViewModel (Application application) {
        super(application);
        mRepository = new DataRepository(application);
        mAllData = mRepository.getAllData();
    }

    public LiveData<List<DataWrapper>> getAllData() { return mAllData; }
    public void insert(DataWrapper data) { mRepository.insert(data); }
}