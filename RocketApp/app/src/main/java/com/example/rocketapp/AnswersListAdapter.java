package com.example.rocketapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AnswersListAdapter extends RecyclerView.Adapter<AnswersListAdapter.ViewHolder> {
    private final ArrayList<Answer> answers;
    private final DataManager.AnswerCallback onClickListener;

    /**
     * QuestionListAdapter is the custom adapter for the recyclerView that displays questions and answers
     * @param answers the initial questions list
     * @param onClickListener
     */
    public AnswersListAdapter(ArrayList<Answer> answers, DataManager.AnswerCallback onClickListener) {
        this.answers = answers;
        this.onClickListener = onClickListener;
    }

    /**
     * @param questions new list of questions
     */
    public void updateList(ArrayList<Answer> questions) {
        this.answers.clear();
        this.answers.addAll(questions);
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
    public AnswersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_recyclerview_item, parent, false);
        return new AnswersListAdapter.ViewHolder(view);
    }

    /**
     * OnClick for the items in the holder implemented
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull AnswersListAdapter.ViewHolder holder, int position) {
        Answer answer = answers.get(position);

        holder.set(answer, view -> {
            onClickListener.callBack(answer);
        });
    }

    /**
     * @return number of experiments in the list
     */
    @Override
    public int getItemCount() {
        Log.e("AnswersAdapter", "Size = " + answers.size());
        return answers.size();
    }

    /**
     * The ViewHolder to populate with experiment information for each experiment.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ownerUsernameTextView;
        private final TextView dialogueTextView;

        /**
         * Sets all fields according to the experiment input
         * @param answer The question to populate the viewholder with
         * @param onClick The click behaviour
         */
        public void set(Answer answer, View.OnClickListener onClick) {
            ownerUsernameTextView.setText(answer.getOwner().getName());
            dialogueTextView.setText(answer.getText());
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerUsernameTextView = itemView.findViewById(R.id.userTextView);
            dialogueTextView = itemView.findViewById(R.id.dialogueTextView);
        }
    }
}
