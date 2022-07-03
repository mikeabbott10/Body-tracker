package it.unipi.sam.volleyballmovementtracker.activities.coach.practices;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.Locale;

import it.unipi.sam.volleyballmovementtracker.activities.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityMiddleBlockerBlockBinding;

public class MiddleBlockerBlockActivity extends SharedElementBaseActivity implements NumberPicker.Formatter, NumberPicker.OnValueChangeListener {
    private ActivityMiddleBlockerBlockBinding binding;
    private int expectedConnectionsNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMiddleBlockerBlockBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        binding.currentTrainingIv.setImageDrawable(
                AppCompatResources.getDrawable(this, currentTrainingDrawableId));

        binding.picker.numberPickerView.setMinValue(1);
        binding.picker.numberPickerView.setMaxValue(10);
        binding.picker.numberPickerView.setFormatter(this);
        binding.picker.numberPickerView.setOnValueChangedListener(this);

        View editView = binding.picker.numberPickerView.getChildAt(0);
        if (editView instanceof EditText) {
            // Remove default input filter
            ((EditText) editView).setFilters(new InputFilter[0]);
        }

        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public String format(int i) {
        return String.format(Locale.ITALY,"%02d", i);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
        expectedConnectionsNumber = newValue;
    }
}
