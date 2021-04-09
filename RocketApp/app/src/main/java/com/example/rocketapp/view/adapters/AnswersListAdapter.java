package com.example.rocketapp.view.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.controller.callbacks.ObjectCallback;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.users.User;

import java.util.ArrayList;

public class AnswersListAdapter extends RecyclerView.Adapter<AnswersListAdapter.ViewHolder> {
    private final ArrayList<Answer> answers;
    private final ObjectCallback<Answer> onClickAnswer;
    private final ObjectCallback<User> onClickUser;

    /**
     * QuestionListAdapter is the custom adapter for the recyclerView that displays questions and answers
     * @param answers the initial questions list
     * @param onClickListener
     */
    public AnswersListAdapter(ArrayList<Answer> answers, ObjectCallback<Answer> onClickListener, ObjectCallback<User> onClickUser) {
        this.answers = answers;
        this.onClickAnswer = onClickListener;
        this.onClickUser = onClickUser;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_answer, parent, false);
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

        holder.set(answer, view -> onClickAnswer.callBack(answer), view-> onClickUser.callBack(answer.getOwner()));
    }

    /**
     * @return number of experiments in the list
     */
    @Override
    public int getItemCount() {
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
         * @param onClickUser The click behaviour when the user name is clicked
         * @param onClickAnswer The click behaviour when the answer is clicked
         */
        public void set(Answer answer, View.OnClickListener onClickAnswer, View.OnClickListener onClickUser) {
            ownerUsernameTextView.setText(answer.getOwner().getName());
            dialogueTextView.setText(answer.getText());
            ownerUsernameTextView.setOnClickListener(onClickUser);
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownerUsernameTextView = itemView.findViewById(R.id.userTextView);
            dialogueTextView = itemView.findViewById(R.id.answerDialogueTextView);
        }
    }
}
