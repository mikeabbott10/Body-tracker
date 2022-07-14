package it.unipi.sam.volleyballmovementtracker.activities.fragments.coach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.sam.volleyballmovementtracker.activities.fragments.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentGetConnectionsBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class CoachPracticingFragment extends CommonFragment {
    private static final String TAG = "FRFRGetConnFragment";
    private FragmentGetConnectionsBinding binding;
    private MyViewModel viewModel;

    public CoachPracticingFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentGetConnectionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.COACH_PRACTICING_FRAGMENT);

        return root;
    }

}
