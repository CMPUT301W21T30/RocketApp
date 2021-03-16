package com.example.rocketapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ExperimentRecyclerViewNotOwnedAdapter extends RecyclerView.Adapter<ExperimentRecyclerViewNotOwnedAdapter.ViewHolder> {

    private static final String TAG = "ExpRecNOAdp";

    private ArrayList<Experiment> experiments;
    private Context context;

    public ExperimentRecyclerViewNotOwnedAdapter(Context context, ArrayList<Experiment> experiments) {
        this.experiments = experiments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.experiment_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        // use the flag to seperate top and bottom
        holder.experimentNameTextView.setText(experiments.get(position).info.getDescription());
        holder.regionTextView.setText(experiments.get(position).info.getRegion());
        holder.experimentListItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + experiments.get(position).info.getDescription());
                Intent expViewintent = new Intent(v.getContext(), ExperimentView.class);
                expViewintent.putExtra("type", experiments.get(position).getType());
                expViewintent.putExtra("description", experiments.get(position).info.getDescription());
                context.startActivity(expViewintent);
                Toast.makeText(context, experiments.get(position).info.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return experiments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView experimentNameTextView;
        TextView regionTextView;
        MaterialCardView experimentListItemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            experimentNameTextView = itemView.findViewById(R.id.experimentNameTextView);
            regionTextView = itemView.findViewById(R.id.regionTextView);
            experimentListItemLayout = itemView.findViewById(R.id.experimentListItemLayout);
        }
    }
}

