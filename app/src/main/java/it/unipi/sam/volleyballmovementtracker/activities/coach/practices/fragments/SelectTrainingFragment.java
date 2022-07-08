package it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentSelectTrainingBinding;
import it.unipi.sam.volleyballmovementtracker.util.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.Training;

public class SelectTrainingFragment extends CommonFragment implements Observer<List<Training>> {
    private FragmentSelectTrainingBinding binding;
    private TrainingsRecyclerViewAdapter adapter;

    public SelectTrainingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectTrainingBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        initInstanceState(savedInstanceState);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.SELECT_TRAINING_FRAGMENT);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        binding.trainingsRv.setLayoutManager(llm);
        binding.trainingsRv.setHasFixedSize(true);
        adapter = new TrainingsRecyclerViewAdapter(new ArrayList<>(), getActivity(), null, null);
        binding.trainingsRv.setAdapter(adapter);
        viewModel.getTrainingList().observe(getViewLifecycleOwner(), this);

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(List<Training> trainings) {
        if(trainings!=null) {
            adapter.setTrainings(trainings);
            // idk quante entries ci sono in più o in meno rispetto a prima (nè dove sono state inserite/eliminate).
            // E' quindi necessario un refresh dell'intero data set:
            adapter.notifyDataSetChanged();
        }
        if(trainings==null || trainings.size()==0){
            Snackbar.make(binding.getRoot(), getString(R.string.no_trainings), 2000).show();
        }
    }
}
