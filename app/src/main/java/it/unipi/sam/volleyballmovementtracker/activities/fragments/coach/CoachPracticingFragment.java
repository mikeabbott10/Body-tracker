package it.unipi.sam.volleyballmovementtracker.activities.fragments.coach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import it.unipi.sam.volleyballmovementtracker.activities.fragments.CommonFragment;
import it.unipi.sam.volleyballmovementtracker.databinding.FragmentGetDataBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.DataWrapper;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;
import it.unipi.sam.volleyballmovementtracker.util.db.DataViewModel;

public class CoachPracticingFragment extends CommonFragment implements Observer<List<DataWrapper>> {
    private static final String TAG = "FRFRGetConnFragment";
    private FragmentGetDataBinding binding;
    private MyViewModel viewModel;

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
    private DataViewModel dataViewModel;

    public CoachPracticingFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentGetDataBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.COACH_PRACTICING_FRAGMENT);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getAllData().observe(getViewLifecycleOwner(), this);

        binding.graph.getViewport().setScrollable(true);
        binding.graph.getViewport().setXAxisBoundsManual(true);

        return root;
    }


    @Override
    public void onChanged(List<DataWrapper> dataWrappers) {
        if(dataWrappers==null || dataWrappers.size()==0)
            return;
        DataWrapper lastDataWrapper = dataWrappers.get(dataWrappers.size()-1); // todo: check se è il primo o l'ultimo inserito
        if(pointsPlotted++ > 500){
            pointsPlotted = 1;
            series.resetData(new DataPoint[]{new DataPoint(0,lastDataWrapper.getData())});
        }

        series.appendData(new DataPoint(pointsPlotted, lastDataWrapper.getData()), true, pointsPlotted);
        binding.graph.getViewport().setMaxX(pointsPlotted);
        binding.graph.getViewport().setMaxX(pointsPlotted<200?0:pointsPlotted-200);
    }
}
