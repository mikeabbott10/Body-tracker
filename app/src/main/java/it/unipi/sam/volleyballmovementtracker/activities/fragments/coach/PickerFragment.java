package it.unipi.sam.volleyballmovementtracker.activities.fragments.coach;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import it.unipi.sam.volleyballmovementtracker.databinding.FragmentNumberPickerBinding;
import it.unipi.sam.volleyballmovementtracker.util.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class PickerFragment extends CommonFragment implements NumberPicker.Formatter, NumberPicker.OnValueChangeListener {
    private FragmentNumberPickerBinding binding;

    public PickerFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNumberPickerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PICKER_FRAGMENT);

        initNumberPicker();

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public String format(int i) {
        return String.format(Locale.ITALY,"%02d", i);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        viewModel.selectCurrentPickerValue(newValue);
    }

    // utils------------------------------------------------
    private void initNumberPicker() {
        binding.picker.numberPickerView.setMinValue(1);
        binding.picker.numberPickerView.setMaxValue(6);
        binding.picker.numberPickerView.setFormatter(this);
        binding.picker.numberPickerView.setOnValueChangedListener(this);
        //try {
        //    binding.picker.numberPickerView.setValue(viewModel.getCurrentPickerValue().getValue());
        //}catch (RuntimeException ignored){}

        // hack bc first element of number picker has been bugged since 2010.....
        View editView = binding.picker.numberPickerView.getChildAt(0);
        if (editView instanceof EditText) {
            // Remove default input filter
            ((EditText) editView).setFilters(new InputFilter[0]);
        }
    }
}
