package com.example.rocketapp.view.activities;

import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.ForumManager;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.users.User;
import com.example.rocketapp.view.adapters.QuestionListAdapter;

public class ExperimentForumActivity extends RocketAppActivity {
    private static final String TAG = "ForumActivity";
    private Experiment experiment;
    private QuestionListAdapter adapter;
    private EditText inputEditText;
    private CommentMode commentMode;
    private Question currentQuestion;
    private Button submitButton;
    private Layer layer;

    enum CommentMode {
        QUESTION,
        ANSWER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_questions);

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));

        inputEditText = findViewById(R.id.commentInput);
        layer = findViewById(R.id.inputLayer);

        RecyclerView questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        adapter = new QuestionListAdapter(experiment.getQuestions(), question -> {
            commentMode = CommentMode.ANSWER;
            currentQuestion = question;
            toggleKeyboard(true);
        }, this::onOwnerClicked);
        questionsRecyclerView.setAdapter(adapter);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        submitButton = findViewById(R.id.submitCommentButton);
        submitButton.setOnClickListener(v -> submitComment());
        ForumManager.listen(experiment, this::onUpdate);
        toggleKeyboard(false);

        // Add question button
        Button button = findViewById(R.id.addQuestionButton);
        button.setOnClickListener(v->{
            if (inputEditText.getVisibility() == View.VISIBLE) {
                submitComment();
                return;
            }
            commentMode = CommentMode.QUESTION;
            toggleKeyboard(true);
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onUpdate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void onOwnerClicked(User user) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(Experiment.ID_KEY, user.getId());
        startActivity(intent);
    }

    private void onUpdate() {
        Log.d(TAG, "onUpdate: " + experiment.getQuestions().toString());
        adapter.updateList(experiment.getQuestions());
    }

    public void submitComment(){
        if (commentMode == CommentMode.ANSWER) {
            Log.e(TAG, "Adding Answer");
            ForumManager.addAnswer(new Answer(inputEditText.getText().toString()), currentQuestion, ()-> {
                Log.e(TAG, "Adding Answer 2");
                Toast.makeText(getApplicationContext(), "Answer added", Toast.LENGTH_SHORT).show();

            }, e->{});
        } else {
            Log.e(TAG, "Adding Question");
            ForumManager.addQuestion(new Question(inputEditText.getText().toString()), experiment, ()-> {
                Toast.makeText(getApplicationContext(), "Question added", Toast.LENGTH_SHORT).show();
            }, e-> {});
        }
        toggleKeyboard(false);
    }

    private void setInputVisibility(int visibility){
        inputEditText.setVisibility(visibility);
        submitButton.setVisibility(visibility);
        layer.setVisibility(visibility);
    }

    @Override
    protected void toggleKeyboard(boolean open){
        super.toggleKeyboard(open);
        if (open){
            setInputVisibility(View.VISIBLE);
            inputEditText.requestFocus();
        } else {
            setInputVisibility(View.INVISIBLE);
            inputEditText.setText("");
        }
    }
}