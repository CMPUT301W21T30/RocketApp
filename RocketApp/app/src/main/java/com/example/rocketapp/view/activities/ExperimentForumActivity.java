package com.example.rocketapp.view.activities;

import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.ForumManager;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.comments.Question;
import com.example.rocketapp.model.users.User;
import com.example.rocketapp.view.QuestionListAdapter;

public class ExperimentForumActivity extends RocketAppActivity {
    private static final String TAG = "ForumActivity";
    private Experiment<?> experiment;
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

        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));

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
        submitButton.setOnClickListener(this::submitComment);
        ForumManager.listen(experiment, this::onUpdate);
        toggleKeyboard(false);

        // Add question button
        Button button = findViewById(R.id.addQuestionButton);
        button.setOnClickListener(v->{
            commentMode = CommentMode.QUESTION;
            toggleKeyboard(true);
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        intent.putExtra("id", user.getId());
        startActivity(intent);
    }

    private void onUpdate(Experiment<?> experiment) {
        adapter.updateList(experiment.getQuestions());
    }

    private void submitComment(View view){
        if (commentMode == CommentMode.ANSWER) {
            ForumManager.addAnswer(new Answer(inputEditText.getText().toString()), currentQuestion, ()-> {}, e->{}
            );
        } else {
            ForumManager.addQuestion(new Question(inputEditText.getText().toString()), experiment, ()-> {}, e-> {});
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