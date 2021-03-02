package com.example.rocketapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void ExampleUsageForDataManager() {
        // Should use the callback lambdas to do work following these methods, since these are asynchronous commands their effects
        // won't exist until after they synchronize with firebase (they will be synchronized at the point when lambdas are called).

//        // Create a new user and login
//        DataManager.createUser("Morty", user -> {
//
//        }, error -> {
//            Log.e(TAG, error.toString());
//        });

        DataManager.login("Morty", user -> {
            // Use this to load the users subscriptions
//            DataManager.pullSubscriptions(experiments -> {
//                Log.d(TAG, "Subscribed experiments:");
//
//            });

            // Publish a new experiment
//            DataManager.publishExperiment(
//                    new Experiment("Marty's second experiment", "An experiment started by Marty", "Canada", 10, true),
//                    experiments -> {
//                Log.d(TAG, "Subscribed experiments:");
//
//            });
//
            // Pull all experiments from firebase
//            DataManager.pullAllExperiments(experiments -> {
//                Log.d(TAG, "All experiments:");
//                for (Experiment e : experiments) {
//                    DataManager.subscribe(e, () -> {
//
//                    });
//                }
//            });

//            // Pull all experiments owned by this user from firebase
//            DataManager.pullOwnedExperiments(experiments -> {
//                for (Experiment experiment : experiments)
//                    DataManager.push(new Question("Here is a question"), experiment, trial -> {});
//            });
//
//            // Pull all subscriptions by this user from firebase
//            DataManager.pullSubscriptions(experiments -> {
//                for (Experiment experiment : experiments)
//                    DataManager.push(new Question("Here is a question"), experiment, trial -> {});
//            });

        }, e -> {
            Log.e(TAG, "User not found.");
        });
    }
}

