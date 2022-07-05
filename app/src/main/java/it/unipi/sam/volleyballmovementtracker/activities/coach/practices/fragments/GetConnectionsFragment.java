package it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Intent> activityResultLaunch;
    public static final int extraDiscoverableDuration = 300;

    public GetConnectionsFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        binding = FragmentGetConnectionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), ((ActivityResultCallback<ActivityResult>)requireActivity()));

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.GET_CONNECTIONS_FRAGMENT);
        viewModel.getMakingMeDiscoverable().observe(getViewLifecycleOwner(), this);
        viewModel.getScanModeStatus().observe(getViewLifecycleOwner(), this);

        initInfoView(binding.infoTapGifIv, binding.infoBtn, binding.infoDescription);

        return root;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onChanged(Object o) {
        if(o instanceof Boolean && (boolean)o){
            // makeMeDiscoverable is now true
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, extraDiscoverableDuration);
            activityResultLaunch.launch(i);
        }else if(o instanceof Integer){
            // scan mode changed (è sempre -1)...todo
            Log.d(TAG, (int)o+"");
        }
    }
}
