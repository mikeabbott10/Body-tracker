package it.unipi.sam.volleyballmovementtracker.activities.player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.sam.volleyballmovementtracker.databinding.FragmentPlayerStartingBinding;
import it.unipi.sam.volleyballmovementtracker.util.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class StartingPlayerFragment extends CommonFragment implements View.OnClickListener {
    private FragmentPlayerStartingBinding binding;
    private MyViewModel viewModel;

    public StartingPlayerFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerStartingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        initInstanceState(savedInstanceState);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PLAYER_STARTING_FRAGMENT);

        binding.middleTvLayout.setOnClickListener(this);

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
    public void onClick(View view){
        if(view.getId() == binding.middleTvLayout.getId()){
            // todo: start PLAYER_PRACTICING_FRAGMENT
            replaceFragment(new PlayerPracticingFragment());
        }
    }

    // utils------------------------------------------------

}