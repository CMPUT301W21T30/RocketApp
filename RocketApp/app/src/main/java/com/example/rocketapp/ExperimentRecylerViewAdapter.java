package com.example.rocketapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExperimentRecylerViewAdapter extends RecyclerView.Adapter<ExperimentRecylerViewAdapter.ViewHolder> {

    private static final String TAG = "ExperimentRecylerViewAd";

    private ArrayList<Experiment> experiments;
    private Context context;

    public ExperimentRecylerViewAdapter(Context context, ArrayList<Experiment> experiments) {
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
