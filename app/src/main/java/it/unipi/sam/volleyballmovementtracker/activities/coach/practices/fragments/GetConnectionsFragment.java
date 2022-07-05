package it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.sam.volleyballmovementtracker.databinding.FragmentGetConnectionsBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class GetConnectionsFragment extends CommonFragment implements Observer<Object> {
    private static final String TAG = "FRFRGetConnFragment";
    private FragmentGetConnectionsBinding binding;
    private MyViewModel viewModel;

    public GetConnectionsFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentGetConnectionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.GET_CONNECTIONS_FRAGMENT);
        viewModel.getImDiscoverable().observe(getViewLifecycleOwner(), this);

        initInfoView(binding.infoTapGifIv, binding.infoBtn, binding.infoDescription);

        return root;
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onChanged(Object o) {
        if(o instanceof Boolean && (boolean)o ){
            //i'm discoverable now

        }
    }
}
