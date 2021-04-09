package com.example.rocketapp;

import android.widget.EditText;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.MockManager;
import com.example.rocketapp.controller.UserManager;
import com.example.rocketapp.model.experiments.Experiment;
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

/**
 * Test user stories in the application
 */
public class GeneralAppTest {
    private Solo solo;
    private final String failedCurrentActivity = "Wrong Activity";
    private final String trial = "TRIAL";
    private final String failure = "FAILURE";
    private final String success = "SUCCESS";
    private final String alberta = "Alberta";
    private final String trueString = "true";
    private final String subscribe = "SUBSCRIBE";
    private final String welcome = "Welcome";

    @Rule
    public MyActivityTestRule rule = new MyActivityTestRule(LoginActivity.class, true, true);


    @Before
    public void setup() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void signIn() {
        solo.assertCurrentActivity(failedCurrentActivity, LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.userNameEditText), "Mock User");
        solo.clickOnText("Submit");
        solo.assertCurrentActivity(failedCurrentActivity, MainActivity.class);
    }

    @Test
    public void checkAddExperiment(){
        String experimentName = "Toss a coin";
        String confirm = "CONFIRM";
        String minTrialInput = "10";
        ExperimentManager.getExperimentArrayList().clear();

        signIn();

        solo.clickOnButton("NEW");
        solo.waitForText("Binomial", 1, 2000);
        solo.pressSpinnerItem(0, 0);
        solo.enterText((EditText) solo.getView(R.id.description_input), experimentName);
        solo.enterText((EditText) solo.getView(R.id.region_input), "AB");
        solo.enterText((EditText) solo.getView(R.id.min_trial), minTrialInput);
        solo.hideSoftKeyboard();
        solo.waitForText(minTrialInput, 1, 2000);
        solo.waitForText(confirm, 1, 2000);
        solo.clickOnText(confirm);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
    }

    @Test
    public void checkAddTrials(){
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.waitForText(trial, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addTrialButton));
        solo.hideSoftKeyboard();
        solo.waitForText(success);
        solo.clickOnView(solo.getView(R.id.addSuccess));
        solo.waitForText(trial, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addTrialButton));
        solo.waitForText(failure, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addFailure));
        solo.waitForText("Number", 1, 2000);

        assertEquals(((TextView) solo.getView(R.id.trialCountTextView)).getText().toString(), "6");
    }

    @Test
    public void checkChangeExperimentState(){
        String notPublished = "Not Published";
        String published = "Published";
        String ended = "Ended";
        String unpublishMenuItem = "Un-publish Experiment";
        String publishMenuItem = "Publish Experiment";
        String endMenuItem = "End Experiment";
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(unpublishMenuItem, 1, 2000);
        solo.clickOnText(unpublishMenuItem);
        solo.waitForText(notPublished, 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.publishedTextView)).getText().toString(), notPublished);
        solo.sendKey(Solo.MENU);
        solo.waitForText(publishMenuItem, 1, 2000);
        solo.clickOnText(publishMenuItem);
        solo.waitForText(published, 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.publishedTextView)).getText().toString(), published);
        solo.sendKey(Solo.MENU);
        solo.waitForText(endMenuItem, 1, 2000);
        solo.clickOnText(endMenuItem);
        solo.waitForText(ended, 1, 2000);
        assertEquals(((TextView) solo.getView(R.id.statusTextView)).getText().toString(), ended);
        assertEquals((solo.getView(R.id.addTrialButton).getVisibility()), 4);
    }

    @Test
    public void checkOpenGraphs(){
        String statisticsMenuItem = "Experiment Statistics";
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(statisticsMenuItem, 1, 2000);
        solo.clickOnText(statisticsMenuItem);
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentStatisticsActivity.class);
    }


    @Test
    public void checkGenerateQRCode(){
        String qrMenuItem = "Generate QR Code";
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(qrMenuItem,1, 2000);
        solo.clickOnText(qrMenuItem);
        solo.assertCurrentActivity(failedCurrentActivity, GenerateQRcodeActivity.class);
        solo.waitForText(success, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addSuccess));
        solo.waitForText(trueString, 1, 2000);
        assertTrue(((TextView) solo.getView(R.id.generatedCodeTextView)).getText().toString().contains("Binomial true"));
    }

    @Test
    public void checkEditExperiment(){
        String editMenuItem = "Edit Experiment";
        String modification = "modified";
        String updateText = "Update";
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(editMenuItem,1, 2000);
        solo.clickOnText(editMenuItem);
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentEditActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editTextRegion), modification);
        solo.waitForText(updateText, 1, 2000);
        solo.clickOnView(solo.getView(R.id.textViewUpdate));
        solo.goBack();
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.waitForText(modification, 1, 2000);
        assertTrue(((TextView) solo.getView(R.id.regionView)).getText().toString().contains(modification));
    }

    @Test
    public void checkIgnoreTrial(){
        String menuItem = "Edit Experiment";
        String expectedMean = "0.33";

        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(menuItem,1, 2000);
        solo.clickOnText(menuItem);

        solo.assertCurrentActivity(failedCurrentActivity, ExperimentEditActivity.class);
        solo.waitForText(trueString, 1, 2000);
        solo.clickOnText(trueString);
        solo.goBack();
        solo.waitForText("Trial Count", 1, 2000);
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.waitForText(expectedMean, 1, 2000);
    }

    @Test
    public void checkEditProfile(){
        String profileMenuItem = "Profile";
        String mockEmail = "mock@gmail.com";
        String mockNumber = "9998887777";
        signIn();

        solo.waitForText(welcome, 1, 2000);
        solo.sendKey(Solo.MENU);
        solo.waitForText(profileMenuItem,1, 2000);
        solo.clickOnText(profileMenuItem);
        solo.assertCurrentActivity(failedCurrentActivity, UserProfileActivity.class);
        solo.enterText((EditText) solo.getView(R.id.userEmailEditText), mockEmail);
        solo.enterText((EditText) solo.getView(R.id.userPhoneNumberEditText), mockNumber);
        solo.clickOnView(solo.getView(R.id.updateProfileButton));
        solo.assertCurrentActivity(failedCurrentActivity, MainActivity.class);
        solo.waitForText(welcome, 1, 2000);
        solo.sendKey(Solo.MENU);
        solo.waitForText(profileMenuItem,1, 2000);
        solo.clickOnText(profileMenuItem);
        solo.assertCurrentActivity(failedCurrentActivity, UserProfileActivity.class);

        assertTrue(((TextView) solo.getView(R.id.userEmailEditText)).getText().toString().contains(mockEmail));
        assertTrue(((TextView) solo.getView(R.id.userPhoneNumberEditText)).getText().toString().contains(mockNumber));
    }

    @Test
    public void checkDiscussionForum(){
        String menuItem = "Discussion Forum";
        String submit = "SUBMIT";
        String question = "Hello there";
        String response = "Howdy";
        String addResponse = "Add Response";
        String addQuestion = "ADD QUESTION";
        signIn();

        solo.waitForText(alberta, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
        solo.sendKey(Solo.MENU);
        solo.waitForText(menuItem,1, 2000);
        solo.clickOnText(menuItem);
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentForumActivity.class);
        solo.waitForText(addQuestion, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.enterText((EditText) solo.getView(R.id.commentInput), question);
        solo.waitForText(question,1, 2000);
        solo.waitForText(submit, 1, 2000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));
        solo.waitForText(question, 1, 2000);
        solo.waitForText(addResponse, 1, 2000);
        solo.clickOnText(addResponse);
        solo.enterText((EditText) solo.getView(R.id.commentInput), response);
        solo.waitForText(response, 1, 2000);
        solo.waitForText(submit, 1, 3000);
        solo.clickOnView(solo.getView(R.id.addQuestionButton));

        assertTrue(((TextView) solo.getView(R.id.dialogueTextView)).getText().toString().contains(question));
        assertTrue(((TextView) solo.getView(R.id.answerDialogueTextView)).getText().toString().contains(response));
    }


    @Test
    public void checkSearchAndSubscribeExperiment(){
        String search = "eggs";
        signIn();
        for (Experiment e: ExperimentManager.getExperimentArrayList()) {
            if (UserManager.getUser().isOwner(e)) {
                ExperimentManager.getExperimentArrayList().remove(e);
                break;
            }
        }
        solo.waitForText(welcome, 1, 2000);
        solo.clickOnButton(subscribe);
        solo.waitForText(search, 1, 2000);
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentSearchActivity.class);
        solo.enterText((EditText) solo.getView(R.id.search_for_experiments), search);
        solo.waitForText(search, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, MainActivity.class);
        solo.waitForText(welcome, 1, 2000);
        solo.clickOnView(solo.getView(R.id.experimentListItemLayout));
        solo.assertCurrentActivity(failedCurrentActivity, ExperimentActivity.class);
    }

    @Test
    public void checkViewUserProfile(){
        checkSearchAndSubscribeExperiment();
        solo.waitForText("Owner", 1, 2000);
        solo.clickOnView(solo.getView(R.id.ownerTextView));
        solo.assertCurrentActivity(failedCurrentActivity, UserProfileActivity.class);
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
