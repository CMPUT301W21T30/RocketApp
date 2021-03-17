package com.example.rocketapp;

import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

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
        //TODO: press on dropdown menu to specify experiment type (defaults to binomial)
        solo.enterText((EditText) solo.getView(R.id.description_input), "automated test");
        solo.enterText((EditText) solo.getView(R.id.region_input), "AB");
        solo.enterText((EditText) solo.getView(R.id.min_trial), "10");
        //TODO: figure out how to tick checkbox
        solo.clickOnButton("PUBLISH");
        //TODO: STARTING FROM HERE TILL LINE BEGINNING WITH END AFTER TODO
        Espresso.pressBack();
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.clickOnButton("LOGIN");
        solo.assertCurrentActivity("Wrong Activity", ExperimentsListActivity.class);
        //TODO: END - these lines are not necessary for testing if owned subscriptions list is updated immediately,
        //TODO: these lines log the user out then log them back in to update the list to make sure list updates
        array = DataManager.getOwnedExperimentsArrayList();
        int finalSize = array.size();
        assertTrue(finalSize == (initialSize + 1));
        //TODO: delete experiment from database then check size again
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
