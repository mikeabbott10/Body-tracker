package it.unipi.sam.volleyballmovementtracker.activities.coach.practices.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.Constants;
import it.unipi.sam.volleyballmovementtracker.util.Training;
import it.unipi.sam.volleyballmovementtracker.util.graphic.ParamRelativeLayout;

public class TrainingsRecyclerViewAdapter extends RecyclerView.Adapter<TrainingsRecyclerViewAdapter.ViewHolder> implements View.OnClickListener{
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cv_name);
            image = itemView.findViewById(R.id.cv_image);
        }
    }

    private Context context;
    private List<Training> trainings;
    private String trainingsPath;
    private String logoPath;

    public TrainingsRecyclerViewAdapter(ArrayList<Training> trainings, Context ctx, String trainingsPath, String logo_path) {
        this.trainings = trainings;
        this.context = ctx;
        // unused:
        this.trainingsPath = Constants.restBasePath + trainingsPath + "/";
        this.logoPath = logo_path;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    @Override
    public int getItemCount() {
        return trainings.size();
    }

    @Override
    public void onClick(View view) {
        Training t = (Training) ((ParamRelativeLayout) view).getObj();
        if( t == null){
            Toast.makeText(context, "Retry later.", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_training, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(i%2==0){
            LinearLayout ll = ((LinearLayout)viewHolder.image.getParent());
            ll.removeView(viewHolder.image);
            ll.addView(viewHolder.image);
        }
        Training t = trainings.get(i);
        viewHolder.name.setText(t.getName());
        //viewHolder.desc.setText(Html.fromHtml(t.getDescription()));
        viewHolder.image.setImageDrawable( getDrawableFromImageCode(t.getLocalImageCode()) );
        ((ParamRelativeLayout) viewHolder.itemView ).setObject(t);
        viewHolder.itemView.setOnClickListener(this);
        //DebugUtility.LogDThis(DebugUtility.TOUCH_OR_CLICK_RELATED_LOG, "AAAA", "pos: "+i, null);
    }

    private Drawable getDrawableFromImageCode(int localImageCode) {
        switch(localImageCode){
            case Constants.MB_BLOCK_IMAGE_ID:
                return AppCompatResources.getDrawable(context, R.drawable.block2);
            case Constants.MBH_BLOCK_IMAGE_ID:
                return AppCompatResources.getDrawable(context, R.drawable.block_plus);
        }
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
