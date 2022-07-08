package it.unipi.sam.volleyballmovementtracker.util;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {
    private final SavedStateHandle savedStateHandle;
    private static final String currentFragmentKey = "cfk";
    private static final String currentNumberPickerKey = "cnpk";
    private static final String imDiscoverableKey = "imdk";
    private static final String scanModeStatusKey = "sssk";
    private static final String trainingListKey = "tlk";

    public MyViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }

    public MutableLiveData<Integer> getCurrentFragment(){
        // Get the LiveData, setting the default value if it doesn't already have a value set.
        return savedStateHandle.getLiveData(currentFragmentKey, 0);
    }
    public void selectCurrentFragment(int frag_id){
        savedStateHandle.set(currentFragmentKey, frag_id);
    }

    public MutableLiveData<Integer> getCurrentPickerValue(){
        return savedStateHandle.getLiveData(currentNumberPickerKey, 0);
    }
    public void selectCurrentPickerValue(int newValue) {
        savedStateHandle.set(currentNumberPickerKey, newValue);
    }

    public MutableLiveData<Boolean> getImDiscoverable(){
        return savedStateHandle.getLiveData(imDiscoverableKey, false);
    }
    public void selectImDiscoverable(boolean newValue) {
        savedStateHandle.set(imDiscoverableKey, newValue);
    }

    public MutableLiveData<Integer> getScanModeStatus(){
        return savedStateHandle.getLiveData(scanModeStatusKey, -1);
    }
    public void selectScanModeStatus(int newValue) {
        savedStateHandle.set(scanModeStatusKey, newValue);
    }

    public MutableLiveData<List<Training>> getTrainingList(){
        return savedStateHandle.getLiveData(trainingListKey, new ArrayList<>());
    }
    public void selectTrainingList(List<Training> newValue) {
        savedStateHandle.set(trainingListKey, newValue);
    }

}
