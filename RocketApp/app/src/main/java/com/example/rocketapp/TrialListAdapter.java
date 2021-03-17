package com.example.rocketapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class TrialListAdapter extends RecyclerView.Adapter<TrialListAdapter.ViewHolder> {

    private static final String TAG = "ExperimentRecylerViewAd";

    private ArrayList<? extends Trial> trials;
    private Context context;

    public TrialListAdapter(Context context, ArrayList<? extends Trial> trials) {
        this.trials = trials;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trial_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        // setting the textView as the trial
        holder.trialTextView.setText(String.valueOf(trials.get(position).getResult()));

    }

    @Override
    public int getItemCount() {
        return trials.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView trialTextView;
        MaterialCardView trialListItemLayout;;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trialTextView = itemView.findViewById(R.id.trialTextView);
            trialListItemLayout = itemView.findViewById(R.id.trialListItemLayout);

        }
    }
}