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

import java.util.ArrayList;
import java.util.LinkedList;
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

    private int pointsPlotted = 0;
    private int maxYValue = 40;
    //private int graphIntervalCounter = 0;

    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
    private DataViewModel dataViewModel;
    private LinkedList<DataWrapper> last_dws_list;

    public CoachPracticingFragment(){}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentGetDataBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // nota requireActivity() : same scope as in the activity is required or different ViewModel!
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.selectCurrentFragment(Constants.COACH_PRACTICING_FRAGMENT);

        series = new LineGraphSeries<>(new DataPoint[]{});
        pointsPlotted = 0;
        last_dws_list = new LinkedList<>();
        binding.graph.getViewport().setScrollable(true);
        binding.graph.getViewport().setXAxisBoundsManual(true);
        binding.graph.getViewport().setYAxisBoundsManual(true);
        binding.graph.getViewport().setMaxY(maxYValue);
        binding.graph.getViewport().setMinY(0);
        binding.graph.addSeries(series);
        if(savedInstanceState!=null){
            // render graph manually
            ArrayList<DataWrapper> aldw = savedInstanceState.getParcelableArrayList(Constants.previous_graph_data_key);
            if(aldw!=null){
                last_dws_list = new LinkedList<>(aldw);
                for(int i = 0; i<100 && i<aldw.size(); ++i){
                    pointsPlotted++;
                    series.appendData(new DataPoint(pointsPlotted, aldw.get(i).getData()), true, pointsPlotted);
                }
                binding.graph.getViewport().setMaxX(pointsPlotted);
                binding.graph.getViewport().setMinX(pointsPlotted-100);
            }
        }

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getLastData().observe(getViewLifecycleOwner(), this);

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if(last_dws_list!=null)
            outState.putParcelableArrayList(Constants.previous_graph_data_key, new ArrayList<>(last_dws_list));
        else
            outState.putParcelableArrayList(Constants.previous_graph_data_key, null);

    }

    @Override
    public void onChanged(List<DataWrapper> dataWrappers) {
        if(dataWrappers==null || dataWrappers.size()==0)
            return;
        assert dataWrappers.size()<=1;
        DataWrapper lastDataWrapper = dataWrappers.get(0);

        // build the last 100 data wrappers in order to handle screen rotation
        if(last_dws_list.size()>100){
            last_dws_list.poll();
        }
        last_dws_list.offer(lastDataWrapper);

        // reset after 500 data
        if(pointsPlotted++ > 500){
            pointsPlotted = 1;
            series.resetData(new DataPoint[]{new DataPoint(0,lastDataWrapper.getData())});
        }

        series.appendData(new DataPoint(pointsPlotted, lastDataWrapper.getData()), true, pointsPlotted);
        binding.graph.getViewport().setMaxX(pointsPlotted);
        binding.graph.getViewport().setMinX(pointsPlotted-100);
    }
}
