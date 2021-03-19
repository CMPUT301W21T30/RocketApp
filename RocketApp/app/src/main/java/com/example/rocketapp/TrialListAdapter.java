package com.example.rocketapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class TrialListAdapter extends RecyclerView.Adapter<TrialListAdapter.ViewHolder> {
    private static final String TAG = "ExperimentRecylerViewAd";
    private ArrayList<Trial> trials;
    private Context context;

    public TrialListAdapter(Context context, ArrayList<Trial> trials) {
        this.trials = trials;
        this.context = context;
    }

    /**
     * @param trials new list of trials
     */
    public void updateList(ArrayList<Trial> trials) {
        this.trials.clear();
        this.trials.addAll(trials);
        notifyDataSetChanged();
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
        holder.trialTextView.setText(String.valueOf(trials.get(position).getValueString()));

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