package it.unipi.sam.volleyballmovementtracker.util;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import it.unipi.sam.volleyballmovementtracker.R;

public class CommonFragment extends Fragment{
    private static final String TAG = "FRFRCommonFragment";
    protected MyViewModel viewModel;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void initInstanceState(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            super.onCreate(savedInstanceState);
        }else {
            super.onCreate(null);
        }
    }

    protected void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_main, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
