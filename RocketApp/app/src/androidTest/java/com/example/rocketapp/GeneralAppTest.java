package com.example.rocketapp;

import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
