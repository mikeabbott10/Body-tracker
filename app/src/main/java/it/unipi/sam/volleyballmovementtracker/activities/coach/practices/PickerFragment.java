package it.unipi.sam.volleyballmovementtracker.activities.coach.practices;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentNumberPickerBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.graphic.GraphicUtil;

public class PickerFragment extends Fragment implements NumberPicker.Formatter,
        NumberPicker.OnValueChangeListener, View.OnClickListener, RequestListener<Drawable> {
    private FragmentNumberPickerBinding binding;
    private MyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNumberPickerBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PICKER_FRAGMENT);

        initNumberPicker();

        Glide
                .with(this)
                .load(R.drawable.info_tap_animated)
                .listener(this)
                .into(binding.infoTapGifIv);
        binding.infoBtn.setOnClickListener(this);

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
        binding.picker.numberPickerView.setMaxValue(10);
        binding.picker.numberPickerView.setFormatter(this);
        binding.picker.numberPickerView.setOnValueChangedListener(this);

        // hack bc first element of number picker has been bugged since 2010.....
        View editView = binding.picker.numberPickerView.getChildAt(0);
        if (editView instanceof EditText) {
            // Remove default input filter
            ((EditText) editView).setFilters(new InputFilter[0]);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == binding.infoBtn.getId()){
            if(binding.infoDescription.getVisibility() != View.VISIBLE){
                // make description visible
                binding.infoBtn.setAlpha(0.5f);
                GraphicUtil.slideDownToOrigin(binding.infoDescription, null, null);
                binding.infoDescription.setVisibility(View.VISIBLE);
            }else{
                // make description invisible
                binding.infoBtn.setAlpha(1.0f);
                GraphicUtil.slideUp(binding.infoDescription, null, null);
                binding.infoDescription.setVisibility(View.INVISIBLE);
            }
        }
    }

    // glide related callbacks
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        ((GifDrawable)resource).setLoopCount(2);
        ((GifDrawable)resource).registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                //do whatever after specified number of loops complete
                ((GifDrawable)resource).stop();
                binding.infoTapGifIv.setVisibility(View.GONE);
            }
        });
        return false;
    }
}
