package com.example.rocketapp;

import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.rocketapp.controller.DataManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.view.activities.ExperimentEditActivity;
import com.example.rocketapp.view.activities.ExperimentsListActivity;
import com.example.rocketapp.view.activities.LogInActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GeneralAppTest {
    private Solo solo;

    @Rule
    public ActivityTestRule <LogInActivity> rule = new ActivityTestRule <LogInActivity>(LogInActivity.class, true, true);

    @Before
    public void setup() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void checkLogin(){
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "saif");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);
    }

    @Test
    public void checkAddExperiment(){
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "saif");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);
        ArrayList<Experiment> array = DataManager.getOwnedExperimentsArrayList();
        int initialSize = array.size();
        solo.clickOnButton("NEW");
        //TODO: figure out how to check if dialog fragment was opened
        solo.pressSpinnerItem(0, 0);
        solo.enterText((EditText) solo.getView(R.id.description_input), "Toss a coin, record SUCCESS if it lands on head, FAIL if it lands on fail");
        solo.enterText((EditText) solo.getView(R.id.region_input), "AB");
        solo.enterText((EditText) solo.getView(R.id.min_trial), "10");
        solo.clickOnCheckBox(0);
        solo.clickOnButton("PUBLISH");
        array = DataManager.getOwnedExperimentsArrayList();
        int finalSize = array.size();
        assertTrue(solo.waitForText("Toss a coin, record SUCCESS if it lands on head, FAIL if it lands on fail", 1, 1000));
        //TODO: delete experiment from database then check size again
    }

    @Test
    public void checkSubscribed() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "archit");
        solo.clickOnButton("LOGIN");
        solo.clickOnButton("SUBSCRIBE");
        solo.clickOnText("Toss a coin");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);
        assertTrue(solo.waitForText("Toss a coin", 1, 1000));
    }

    @Test
    public void checkAddTrial() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "archit");
        solo.clickOnButton("LOGIN");
        if (!solo.searchText("Toss a coin", 1)) {
            solo.clickOnButton("SUBSCRIBE");
            solo.clickOnText("Toss a coin");
            solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);
            assertTrue(solo.waitForText("Toss a coin", 1, 1000));
        }
        solo.clickOnText("Toss a coin");
        solo.clickOnButton("Add Trial");
        solo.clickOnButton("Success");
        //TODO check if trial was added
    }


    @Test
    public void checkPublished() {
        // Login
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "Mike Greber");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);

        // Slide Experiment to published
        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        View row = solo.getText("Coin flip experiment");
        row.getLocationInWindow(location);

        // fail if the view with text cannot be located in the window
        if (location.length == 0) {
            fail("Could not find text: " + "Throw distance experiment");
        }

        fromX = location[0] + 100;
        fromY = location[1];

        toX = location[0];
        toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 10);

        // check published
        assertTrue(solo.searchText("PUBLISHED"));;
    }

    @Test
    public void checkUnPublished() {
        // Login
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "Mike Greber");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);

        // Slide Experiment to published
        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        View row = solo.getText("Coin flip experiment");
        row.getLocationInWindow(location);

        // fail if the view with text cannot be located in the window
        if (location.length == 0) {
            fail("Could not find text: " + "Throw distance experiment");
        }

        fromX = location[0];
        fromY = location[1];

        toX = location[0] + 100;
        toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 10);

        // check published
        assertTrue(solo.searchText("UNPUBLISHED"));;

        // Reset to publish activity by default
        fromX = location[0] + 100;
        fromY = location[1];

        toX = location[0];
        toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 10);
        assertTrue(solo.searchText("PUBLISHED"));;
    }


    @Test
    public void testOwnerActivity() {
        // Login
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "Mike Greber");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);

        // Check owned experiment
        solo.clickOnText("Throw distance experiment");
        solo.assertCurrentActivity("Wrong Activity", ExperimentEditActivity.class);
        assertTrue(solo.searchText("Measurement"));
    }


    @Test
    public void testOwnerActivityTrial() {
        // Login
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "Mike Greber");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);

        // Check owned experiment
        solo.clickOnText("Throw distance experiment");
        solo.assertCurrentActivity("Wrong Activity", ExperimentEditActivity.class);
        assertTrue(solo.searchText("Measurement"));
        // check if the trial exists
        assertTrue(solo.searchText("1234.0"));
    }

    @Test
    public void testOwnerActivityEndExperiment() {
        // Login
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "Mike Greber");
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);

        // Pushed the experiment
        // Slide Experiment to published
        int fromX, toX, fromY, toY;
        int[] location = new int[2];

        View row = solo.getText("Throw distance experiment");
        row.getLocationInWindow(location);

        // fail if the view with text cannot be located in the window
        if (location.length == 0) {
            fail("Could not find text: " + "Throw distance experiment");
        }

        fromX = location[0] + 100;
        fromY = location[1];

        toX = location[0];
        toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 10);

        // check published
        assertTrue(solo.searchText("PUBLISHED"));;


        // Check owned experiment
        solo.clickOnText("Throw distance experiment");
        solo.assertCurrentActivity("Wrong Activity", ExperimentEditActivity.class);

        // check if the trial exists
        solo.clickOnView(solo.getView(R.id.EndExperimentBtn));
        assertTrue(solo.searchText("ENDED"));
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
