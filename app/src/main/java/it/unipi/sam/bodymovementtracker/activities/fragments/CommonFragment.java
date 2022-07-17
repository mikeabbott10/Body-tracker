package it.unipi.sam.bodymovementtracker.activities.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.util.MyViewModel;

public class CommonFragment extends Fragment{
    private static final String TAG = "FRFRCommonFragment";
    protected MyViewModel viewModel;

    protected void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_main, someFragment);
        transaction.commit();
    }
}
