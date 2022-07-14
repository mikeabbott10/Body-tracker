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

import java.util.HashSet;
import java.util.Set;

import it.unipi.sam.volleyballmovementtracker.activities.PlayerPracticingActivity;
import it.unipi.sam.volleyballmovementtracker.activities.fragments.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentLoadRecyclerViewBinding;
import it.unipi.sam.volleyballmovementtracker.util.BTDevicesRecyclerViewAdapter;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class PlayerPracticingFragment extends CommonFragment implements Observer<Set<BluetoothDevice>>, View.OnClickListener {
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

        hideConnectingContainer();
        binding.loadingPanel.setVisibility(View.VISIBLE);

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.PLAYER_PRACTICING_FRAGMENT);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        binding.rv.setLayoutManager(llm);
        binding.rv.setHasFixedSize(true);
        adapter = new BTDevicesRecyclerViewAdapter(new HashSet<>(), getActivity(), ((PlayerPracticingActivity)requireActivity()), this);
        binding.rv.setAdapter(adapter);
        ((PlayerPracticingActivity)requireActivity()).currentFoundDevicesList.observe(requireActivity(), this);

        ((PlayerPracticingActivity)requireActivity()).mBoundService.getLive_bt_state().observe(requireActivity(), btState -> {
            switch(btState) {
                case Constants.BT_STATE_CONNECTED: {
                    showConnected();
                    break;
                }
                case Constants.BT_STATE_CONNECTION_FAILED:{
                    hideConnectingContainer();
                    break;
                }
            }
        });

        binding.closeBtn.setOnClickListener(this);

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
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(Set<BluetoothDevice> btDevices) {
        if(btDevices!=null) {
            Log.d(TAG, "found btDevices changed");
            adapter.setBtDevices(btDevices);
            // idk quante entries ci sono in più o in meno rispetto a
            // prima (nè dove sono state inserite/eliminate).
            // E' quindi necessario un refresh dell'intero data set:
            adapter.notifyDataSetChanged();
            binding.loadingPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        ((PlayerPracticingActivity)requireActivity()).myStopService();
    }

    // utils ---------------------------------------------------------------------------------------
    private void hideConnectingContainer(){
        binding.connectionTvContainer.setVisibility(View.GONE);
        binding.connectingTv.setVisibility(View.VISIBLE);
        binding.connectedTv.setVisibility(View.INVISIBLE);
    }
    public void showConnecting(){
        binding.connectionTvContainer.setVisibility(View.VISIBLE);
        binding.connectingTv.setVisibility(View.VISIBLE);
        binding.connectedTv.setVisibility(View.INVISIBLE);
    }
    private void showConnected(){
        binding.connectionTvContainer.setVisibility(View.VISIBLE);
        binding.connectingTv.setVisibility(View.INVISIBLE);
        binding.connectedTv.setVisibility(View.VISIBLE);
    }


}
