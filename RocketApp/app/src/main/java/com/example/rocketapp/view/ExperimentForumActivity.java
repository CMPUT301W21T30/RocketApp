package com.example.rocketapp.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.DataManager;
import com.example.rocketapp.model.comments.Answer;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.comments.Question;

public class ExperimentForumActivity extends AppCompatActivity {
    private static final String TAG = "QuestionsActivity";
    private Experiment experiment;
    private QuestionListAdapter adapter;
    private EditText inputEditText;
    private InputMethodManager input;
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

        experiment = DataManager.getExperiment(getIntent().getSerializableExtra("id"));

        inputEditText = findViewById(R.id.commentInput);
        input = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        layer = findViewById(R.id.inputLayer);

        RecyclerView questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        adapter = new QuestionListAdapter(this, experiment.getQuestions(), question -> {
            commentMode = CommentMode.ANSWER;
            currentQuestion = question;
            toggleKeyboard(true);
        }, user -> {
                    // TODO put whatever you want to do when user is clicked here.
        });
        questionsRecyclerView.setAdapter(adapter);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        submitButton = findViewById(R.id.submitCommentButton);
        submitButton.setOnClickListener(this::submitComment);
        DataManager.listen(experiment, this::onUpdate);
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


    private void onUpdate(Experiment experiment) {
        adapter.updateList(experiment.getQuestions());
    }

    public void submitComment(View view){
        if (commentMode == CommentMode.ANSWER) {
            DataManager.addAnswer(new Answer(inputEditText.getText().toString()), currentQuestion, ()-> {}, e->{}
            );
        } else {
            DataManager.addQuestion(new Question(inputEditText.getText().toString()), experiment, ()-> {}, e-> {});
        }
        toggleKeyboard(false);
    }

    private void setInputVisibility(int visibility){
        inputEditText.setVisibility(visibility);
        submitButton.setVisibility(visibility);
        layer.setVisibility(visibility);
    }

    private void toggleKeyboard(boolean open){
        if (open){
            setInputVisibility(View.VISIBLE);
            input.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            inputEditText.requestFocus();
        } else {
            setInputVisibility(View.INVISIBLE);
            input.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
            inputEditText.setText("");
        }
    }
}