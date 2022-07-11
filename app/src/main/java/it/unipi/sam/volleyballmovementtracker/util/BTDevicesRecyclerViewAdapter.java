package it.unipi.sam.volleyballmovementtracker.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unipi.sam.volleyballmovementtracker.R;
import it.unipi.sam.volleyballmovementtracker.util.graphic.ParamRelativeLayout;

public class BTDevicesRecyclerViewAdapter extends RecyclerView.Adapter<BTDevicesRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener{
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cv_name);
            desc = itemView.findViewById(R.id.cv_description);
        }
    }

    private final Context context;
    private List<BluetoothDevice> btDevices;

    public BTDevicesRecyclerViewAdapter(ArrayList<BluetoothDevice> btDevices, Context ctx) {
        this.btDevices = btDevices;
        this.context = ctx;
    }

    public void setBtDevices(List<BluetoothDevice> btDevices) {
        this.btDevices = btDevices;
    }

    @Override
    public int getItemCount() {
        return btDevices.size();
    }

    @Override
    public void onClick(View view) {
        BluetoothDevice btd = (BluetoothDevice) ((ParamRelativeLayout) view).getObj();
        if( btd == null){
            Toast.makeText(context, "Retry later.", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_btdevice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BluetoothDevice btd = btDevices.get(i);
        try {
            viewHolder.name.setText(btd.getName());
        }catch(SecurityException ignored){}
        viewHolder.desc.setText(btd.getAddress());
        ((ParamRelativeLayout) viewHolder.itemView ).setObject(btd);
        viewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
