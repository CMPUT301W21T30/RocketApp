package com.example.rocketapp.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocketapp.R;
import com.example.rocketapp.model.trials.Trial;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

/**
 * The trial list adapter that is used for showing the trials for a given experiment
 */
public class TrialListAdapter extends RecyclerView.Adapter<TrialListAdapter.ViewHolder> {
    private static final String TAG = "ExperimentRecylerViewAd";
    private ArrayList<Trial> trials;
    private Context context;

    /**
     *
     * @param context context of the given experiment
     * @param trials trials for the given experiment
     */
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
    /**
     * creating the view holder for the adapter
     * @param parent parent of the view
     * @param viewType type of the view
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trial_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    /**
     * binding the view holder for the adapter
     * @param holder the view holder
     * @param position position for the item in the list
     */
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        // setting the textView as the trial
        holder.trialTextView.setText(String.valueOf(trials.get(position).getValueString()));
        holder.trialListItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trials.get(position).getIgnored()) {
                    trials.get(position).setIgnored(false);
                    int backgroundColor = ContextCompat.getColor(context, R.color.dark_green);
                    holder.trialListItemLayout.setCardBackgroundColor(backgroundColor);
                    Toast.makeText(context, "Include Trial: " + trials.get(position).getValueString(), Toast.LENGTH_SHORT).show();
                } else {
                    trials.get(position).setIgnored(true);
                    holder.trialListItemLayout.setCardBackgroundColor(Color.RED);
                    Toast.makeText(context, "Ignore Trial: " + trials.get(position).getValueString(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    /**
     * @return size of the adapter
     */
    public int getItemCount() {
        return trials.size();
    }

    /**
     *  The view holder for each item in the recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView trialTextView;
        MaterialCardView trialListItemLayout;;

        /**
         *  implementing the view holder
         * @param itemView the item that we are going to view on the list
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trialTextView = itemView.findViewById(R.id.trialTextView);
            trialListItemLayout = itemView.findViewById(R.id.trialListItemLayout);

        }
    }
}