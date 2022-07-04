package it.unipi.sam.volleyballmovementtracker.activities.coach.practices;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.activities.SharedElementBaseActivity;
import it.unipi.sam.volleyballmovementtracker.databinding.ActivityPracticingBinding;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.MyViewModel;

public class PracticingActivity extends SharedElementBaseActivity implements View.OnClickListener, Observer<Integer> {
    private ActivityPracticingBinding binding;
    private MyViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPracticingBinding.inflate(getLayoutInflater());
        binding.whoAmIIv.setImageDrawable(
                AppCompatResources.getDrawable(this, whoAmIDrawableId));
        binding.currentTrainingIv.setImageDrawable(
                AppCompatResources.getDrawable(this, currentTrainingDrawableId));
        binding.bluetoothState.setImageDrawable(
                AppCompatResources.getDrawable(this, currentBtStateDrawableId));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getCurrentFragment().observe(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.fragmentContainerMain.setVisibility(View.VISIBLE);
        // While your activity is in the STARTED lifecycle state
        // or higher, fragments can be added, replaced, or removed
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, getFragmentClassFromModel(), null)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void onBackPressed() {
        binding.fragmentContainerMain.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        // TODO: Click su "custom status bar" porta indietro
    }

    @Override
    public void onChanged(Integer fragment_id) {
        // TODO: aggiornare icona bluetooth status quando cambia currentFragment (MyViewModel)
    }

    // utils--------------------------------------------------------
    private Class<? extends androidx.fragment.app.Fragment> getFragmentClassFromModel() {
        try{
            switch(viewModel.getCurrentFragment().getValue()){
                case Constants.PICKER_FRAGMENT:
                    return PickerFragment.class;
            }
        }catch (RuntimeException ignored){}
        return PickerFragment.class;
    }
}
