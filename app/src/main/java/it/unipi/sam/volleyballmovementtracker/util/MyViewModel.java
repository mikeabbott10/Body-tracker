package it.unipi.sam.volleyballmovementtracker.util;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private final SavedStateHandle savedStateHandle;
    private static final String currentFragmentKey = "cfk";
    private static final String currentNumberPickerKey = "cnpk";

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
        // Get the LiveData, setting the default value if it doesn't already have a value set.
        return savedStateHandle.getLiveData(currentNumberPickerKey, 0);
    }
    public void selectCurrentPickerValue(int newValue) {
        savedStateHandle.set(currentNumberPickerKey, newValue);
    }
}
