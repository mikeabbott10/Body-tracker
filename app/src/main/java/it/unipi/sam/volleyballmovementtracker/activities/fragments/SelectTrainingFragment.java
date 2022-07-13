package it.unipi.sam.volleyballmovementtracker.activities.fragments;

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

import it.unipi.sam.volleyballmovementtracker.databinding.FragmentLoadRecyclerViewBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.Training;
import it.unipi.sam.volleyballmovementtracker.util.TrainingsRecyclerViewAdapter;

public class SelectTrainingFragment extends CommonFragment implements Observer<List<Training>> {
    private static final String TAG = "FRFRSelecTraininFragm";
    private FragmentLoadRecyclerViewBinding binding;
    private TrainingsRecyclerViewAdapter adapter;

    public SelectTrainingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentLoadRecyclerViewBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        binding.loadingPanel.setVisibility(View.VISIBLE);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PLAYER_STARTING_FRAGMENT);


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        binding.rv.setLayoutManager(llm);
        binding.rv.setHasFixedSize(true);
        adapter = new TrainingsRecyclerViewAdapter(new ArrayList<>(), getActivity());
        binding.rv.setAdapter(adapter);
        viewModel.getTrainingList().observe(getViewLifecycleOwner(), this);

        return root;
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(List<Training> trainings) {
        if(trainings!=null) {
            adapter.setTrainings(trainings);
            // idk quante entries ci sono in più o in meno rispetto a prima (nè dove sono state inserite/eliminate).
            // E' quindi necessario un refresh dell'intero data set:
            adapter.notifyDataSetChanged();
            binding.loadingPanel.setVisibility(View.GONE);
        }
    }

    // utils------------------------------------------------

}
