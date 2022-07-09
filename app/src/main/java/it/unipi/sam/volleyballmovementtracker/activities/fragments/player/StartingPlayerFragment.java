package it.unipi.sam.volleyballmovementtracker.activities.fragments.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.databinding.FragmentPlayerStartingBinding;
import it.unipi.sam.volleyballmovementtracker.util.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.Training;
import it.unipi.sam.volleyballmovementtracker.util.TrainingsRecyclerViewAdapter;

public class StartingPlayerFragment extends CommonFragment implements View.OnClickListener, Observer<List<Training>> {
    private static final String TAG = "FRFRStartPlayeFragm";
    private FragmentPlayerStartingBinding binding;
    private TrainingsRecyclerViewAdapter adapter;

    public StartingPlayerFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        binding = FragmentPlayerStartingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        binding.selectTrainingsPart.loadingPanel.setVisibility(View.VISIBLE);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PLAYER_STARTING_FRAGMENT);

        binding.startingTvLayout.setOnClickListener(this);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        binding.selectTrainingsPart.trainingsRv.setLayoutManager(llm);
        binding.selectTrainingsPart.trainingsRv.setHasFixedSize(true);
        adapter = new TrainingsRecyclerViewAdapter(new ArrayList<>(), getActivity());
        binding.selectTrainingsPart.trainingsRv.setAdapter(adapter);
        viewModel.getTrainingList().observe(getViewLifecycleOwner(), this);

        return root;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onClick(View view){
        if(view.getId() == binding.startingTvLayout.getId()){
            replaceFragment(new PlayerPracticingFragment());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(List<Training> trainings) {
        if(trainings!=null) {
            adapter.setTrainings(trainings);
            // idk quante entries ci sono in più o in meno rispetto a prima (nè dove sono state inserite/eliminate).
            // E' quindi necessario un refresh dell'intero data set:
            adapter.notifyDataSetChanged();
            binding.selectTrainingsPart.loadingPanel.setVisibility(View.GONE);
        }
    }

    // utils------------------------------------------------

}