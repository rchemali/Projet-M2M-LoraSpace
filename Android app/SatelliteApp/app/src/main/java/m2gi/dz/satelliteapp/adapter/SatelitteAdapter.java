package m2gi.dz.satelliteapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import m2gi.dz.satelliteapp.R;
import m2gi.dz.satelliteapp.model.Satellite;

/**
 * Created by chemali on 24/03/2018.
 */

public class SatelitteAdapter extends RecyclerView.Adapter<SatelitteAdapter.MyViewHolder> {

private ArrayList<Satellite> satellites;

    public SatelitteAdapter(ArrayList<Satellite> satellites){

        this.satellites = satellites;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sat,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Satellite satellite = satellites.get(position);
        holder.namesat.setText(satellite.getName());
        holder.idsat.setText(satellite.getId());

    }

    @Override
    public int getItemCount() {
        return satellites.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView namesat, idsat ;

    public MyViewHolder(View itemView) {
        super(itemView);

        namesat = (TextView)itemView.findViewById(R.id.name);
        idsat = (TextView)itemView.findViewById(R.id.idsat);

    }
}










}







