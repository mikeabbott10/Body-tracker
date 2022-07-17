package it.unipi.sam.bodymovementtracker.util;

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
import java.util.Set;

import it.unipi.sam.bodymovementtracker.R;
import it.unipi.sam.bodymovementtracker.activities.fragments.player.PlayerPracticingFragment;
import it.unipi.sam.bodymovementtracker.util.bluetooth.OnFoundDeviceSelectedListener;
import it.unipi.sam.bodymovementtracker.util.graphic.GraphicUtil;
import it.unipi.sam.bodymovementtracker.util.graphic.ParamRelativeLayout;

public class BTDevicesRecyclerViewAdapter extends RecyclerView.Adapter<BTDevicesRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener{
    //private static final String TAG = "CLCLBtDevicRecycVieAdap";

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
    private final OnFoundDeviceSelectedListener onFoundDeviceSelectedListener;
    private PlayerPracticingFragment playerPracticingFragment;
    private List<BluetoothDevice> btDevices;

    public BTDevicesRecyclerViewAdapter(Set<BluetoothDevice> btDevices, Context ctx, OnFoundDeviceSelectedListener onFoundDeviceSelectedListener, PlayerPracticingFragment playerPracticingFragment) {
        this.btDevices = new ArrayList<>(btDevices);
        this.context = ctx;
        this.onFoundDeviceSelectedListener = onFoundDeviceSelectedListener;
        this.playerPracticingFragment = playerPracticingFragment;
    }

    public void setBtDevices(Set<BluetoothDevice> btDevices) {
        this.btDevices = new ArrayList<>(btDevices);
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
        GraphicUtil.scaleImage(context, view, -1, null);
        playerPracticingFragment.showConnecting();
        onFoundDeviceSelectedListener.onDeviceSelected(btd);
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
        ((ParamRelativeLayout) viewHolder.itemView).setObject(btd);
        viewHolder.itemView.setOnClickListener(this);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
