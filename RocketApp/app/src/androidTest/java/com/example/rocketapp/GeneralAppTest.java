package com.example.rocketapp;

import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.android.dx.command.Main;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.MockExperimentManager;
import com.example.rocketapp.controller.MockManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.experiments.MeasurementExperiment;
import com.example.rocketapp.view.activities.ExperimentActivity;
import com.example.rocketapp.view.activities.ExperimentEditActivity;
import com.example.rocketapp.view.activities.ExperimentForumActivity;
import com.example.rocketapp.view.activities.ExperimentSearchActivity;
import com.example.rocketapp.view.activities.ExperimentStatisticsActivity;
import com.example.rocketapp.view.activities.GenerateQRcodeActivity;
import com.example.rocketapp.view.activities.LoginActivity;
import com.example.rocketapp.view.activities.MainActivity;
import com.example.rocketapp.view.activities.UserProfileActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


class MyActivityTestRule extends ActivityTestRule<LoginActivity> {
    public MyActivityTestRule(Class activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void beforeActivityLaunched() {
        MockManager.initializeMock();
        super.beforeActivityLaunched();
    }
}

public class GeneralAppTest {
    private Solo solo;

    @Rule
    public MyActivityTestRule rule = new MyActivityTestRule(LoginActivity.class, true, true);


    @Before
    public void setup() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void signIn() {
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.userNameEditText), "Mock User");
        solo.clickOnText("Submit");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void checkAddExperiment(){
        String experimentName = "Toss a coin";
        ExperimentManager.getExperimentArrayList().clear();

        signIn();

        solo.clickOnButton("NEW");
        solo.waitForText("Binomial", 1, 2000);
        solo.pressSpinnerItem(0, 0);
        solo.enterText((EditText) solo.getView(R.id.description_input), experimentName);
        solo.enterText((EditText) solo.getView(R.id.region_input), "AB");
        solo.enterText((EditText) solo.getView(R.id.min_trial), "10");
        solo.hideSoftKeyboard();
        solo.waitForText("10", 1, 2000);
        solo.waitForText("CONFIRM", 1, 2000);
        solo.clickOnText("CONFIRM");
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
    }

    @Test
    public void checkAddTrials(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.waitForText("TRIAL", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addTrialButton));
        solo.hideSoftKeyboard();
        solo.waitForText("SUCCESS");
        solo.clickOnView(solo.getView(R.id.addSuccess));
        solo.waitForText("TRIAL", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addTrialButton));
        solo.waitForText("FAILURE", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addFailure));
        solo.waitForText("Number", 1, 2000);

        assertEquals(((TextView) solo.getView(R.id.trialCountTextView)).getText().toString(), "6");
    }

    @Test
    public void checkChangeExperimentState(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Un-publish Experiment");
        solo.waitForText("Not published", 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.publishedTextView)).getText().toString(), "Not Published");
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Publish Experiment");
        solo.waitForText("Published", 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.publishedTextView)).getText().toString(), "Published");
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("End Experiment");
        solo.waitForText("Ended", 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.statusTextView)).getText().toString(), "Ended");
        assertEquals((solo.getView(R.id.addTrialButton).getVisibility()), 4);
    }

    @Test
    public void checkOpenGraphs(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Experiment Statistics");
        solo.assertCurrentActivity("Wrong Activity", ExperimentStatisticsActivity.class);
    }


    @Test
    public void checkGenerateQRCode(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Generate QR Code");
        solo.assertCurrentActivity("Wrong Activity", GenerateQRcodeActivity.class);
        solo.waitForText("SUCCESS", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addSuccess));
        solo.waitForText("true", 1, 2000);
        assertTrue(((TextView) solo.getView(R.id.generatedCodeTextView)).getText().toString().contains("Binomial true"));
    }

    @Test
    public void checkEditExperiment(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Edit Experiment");
        solo.assertCurrentActivity("Wrong Activity", ExperimentEditActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editTextRegion), " modified");
        solo.waitForText("Update", 1, 2000);
        solo.clickOnView(solo.getView(R.id.textViewUpdate));
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.waitForText("modified", 1, 2000);
        assertTrue(((TextView) solo.getView(R.id.regionView)).getText().toString().contains("modified"));
    }

    @Test
    public void checkIgnoreTrial(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Edit Experiment");
        solo.assertCurrentActivity("Wrong Activity", ExperimentEditActivity.class);
        solo.waitForText("true", 1, 2000);
        solo.clickOnText("true");
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.waitForText("Trial Count", 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.trialCountTextView)).getText().toString(), "3");
        assertTrue(((TextView) solo.getView(R.id.meanView)).getText().toString().contains("0.333"));

    }

    @Test
    public void checkEditProfile(){
        signIn();

        solo.waitForText("Welcome", 1, 2000);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Profile");

        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
        solo.enterText((EditText) solo.getView(R.id.userEmailEditText), "mock@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.userPhoneNumberEditText), "9998887777");
        solo.clickOnView(solo.getView(R.id.updateProfileButton));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.waitForText("Welcome", 1, 2000);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Profile");
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);

        assertTrue(((TextView) solo.getView(R.id.userEmailEditText)).getText().toString().contains("mock@gmail.com"));
        assertTrue(((TextView) solo.getView(R.id.userPhoneNumberEditText)).getText().toString().contains("9998887777"));
    }

    @Test
    public void checkDiscussionForum(){
        signIn();

        solo.waitForText("Alberta", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.clickOnMenuItem("Discussion Forum");
        solo.assertCurrentActivity("Wrong Activity", ExperimentForumActivity.class);
        solo.waitForText("ADD QUESTION", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.enterText((EditText) solo.getView(R.id.commentInput), "Hello there");
        solo.waitForText("Hello there",1, 2000);
        solo.waitForText("SUBMIT", 1, 2000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.waitForText("Hello there", 1, 2000);
        solo.waitForText("Add Response", 1, 2000);
        solo.clickOnText("Add Response");
        solo.enterText((EditText) solo.getView(R.id.commentInput), "Howdy");
        solo.waitForText("Howdy", 1, 2000);
        solo.waitForText("SUBMIT", 1, 3000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));

        assertTrue(((TextView) solo.getView(R.id.dialogueTextView)).getText().toString().contains("Hello there"));
        assertTrue(((TextView) solo.getView(R.id.answerDialogueTextView)).getText().toString().contains("Howdy"));
    }


    @Test
    public void checkSearchAndSubscribeExperiment(){
        signIn();
        for (Experiment e: ExperimentManager.getExperimentArrayList()) {
            if (UserManager.getUser().isOwner(e)) {
                ExperimentManager.getExperimentArrayList().remove(e);
                break;
            }
        }
        solo.waitForText("Welcome", 1, 2000);
        solo.clickOnButton("SUBSCRIBE");
        solo.assertCurrentActivity("Wrong Activity", ExperimentSearchActivity.class);
        solo.enterText((EditText) solo.getView(R.id.search_for_experiments), "eggs");
        solo.waitForText("eggs", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.waitForText("Welcome", 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
    }

    @Test
    public void checkViewUserProfile(){
        checkSearchAndSubscribeExperiment();
        solo.waitForText("Owner", 1, 2000);
        solo.clickOnView(solo.getView(R.id.ownerTextView));
        solo.assertCurrentActivity("Wrong Activity", UserProfileActivity.class);
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
