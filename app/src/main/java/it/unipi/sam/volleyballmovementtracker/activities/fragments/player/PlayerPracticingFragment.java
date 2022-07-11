package it.unipi.sam.volleyballmovementtracker.activities.fragments.player;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
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

import it.unipi.sam.volleyballmovementtracker.activities.fragments.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentLoadRecyclerViewBinding;
import it.unipi.sam.volleyballmovementtracker.util.BTDevicesRecyclerViewAdapter;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class PlayerPracticingFragment extends CommonFragment implements Observer<List<BluetoothDevice>> {
    private static final String TAG = "FRFRPlayePractFragm";
    private FragmentLoadRecyclerViewBinding binding;
    private BTDevicesRecyclerViewAdapter adapter;

    public PlayerPracticingFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentLoadRecyclerViewBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        binding.loadingPanel.setVisibility(View.VISIBLE);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PLAYER_PRACTICING_FRAGMENT);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        binding.rv.setLayoutManager(llm);
        binding.rv.setHasFixedSize(true);
        adapter = new BTDevicesRecyclerViewAdapter(new ArrayList<>(), getActivity());
        binding.rv.setAdapter(adapter);
        viewModel.getBtDevicesList().observe(requireActivity(), this);
        return super.onCreateView(inflater,container,savedInstanceState);
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
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(List<BluetoothDevice> btDevices) {
        if(btDevices!=null) {
            adapter.setBtDevices(btDevices);
            // idk quante entries ci sono in più o in meno rispetto a prima (nè dove sono state inserite/eliminate).
            // E' quindi necessario un refresh dell'intero data set:
            adapter.notifyDataSetChanged();
            binding.loadingPanel.setVisibility(View.GONE);
        }
    }
}
