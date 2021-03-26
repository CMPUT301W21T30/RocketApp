package com.example.rocketapp.view;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;

/**
 * Adapter for displaying experiments list in a recycler view.
 */
public class ExperimentListAdapter extends RecyclerView.Adapter<ExperimentListAdapter.ViewHolder> {
    private final ArrayList<Experiment> experiments;
    private final ExperimentManager.ExperimentCallback onClickListener;

    /**
     * ExperimentListAdapter is the custom adapter for the recyclerView that displays searched experiments
     * @param experiments the initial experiment list
     * @param onClickListener
     */
    public ExperimentListAdapter(ArrayList<Experiment> experiments, ExperimentManager.ExperimentCallback onClickListener) {
        this.experiments = experiments;
        this.onClickListener = onClickListener;
    }

    /**
     * @param experiments new list of experiments
     */
    public void updateList(ArrayList<Experiment> experiments) {
        this.experiments.clear();
        this.experiments.addAll(experiments);
        notifyDataSetChanged();
    }

    /**
     * Viewholder for experiment_recycler_view_item.
     * Multiple views can be populated into the holder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.experiment_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * OnClick for the items in the holder implemented
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Experiment experiment = experiments.get(position);

        holder.set(experiment, view -> {
            onClickListener.callBack(experiment);
        });
    }


    /**
     * @return number of experiments in the list
     */
    @Override
    public int getItemCount() {
        return experiments.size();
    }

    /**
     * The ViewHolder to populate with experiment information for each experiment.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView experimentNameTextView;
        private final TextView regionTextView;
        private final TextView statusTextView;
        private final TextView ownerTextView;
        private final MaterialCardView experimentListItemLayout;

        /**
         * Sets all fields according to the experiment input
         * @param experiment The experiment to populate the viewholder with
         * @param onClick The click behaviour
         */
        public void set(Experiment experiment, View.OnClickListener onClick) {
            experimentNameTextView.setText(experiment.info.getDescription());
            regionTextView.setText(experiment.info.getRegion());
            ownerTextView.setText(experiment.getOwner().getName());
            statusTextView.setText(experiment.getState().toString());
            experimentListItemLayout.setOnClickListener(onClick);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            experimentNameTextView = itemView.findViewById(R.id.experimentNameTextView);
            regionTextView = itemView.findViewById(R.id.regionTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            ownerTextView = itemView.findViewById(R.id.ownerTextView);
            experimentListItemLayout = itemView.findViewById(R.id.experimentListItemLayout);
        }


    }



}
