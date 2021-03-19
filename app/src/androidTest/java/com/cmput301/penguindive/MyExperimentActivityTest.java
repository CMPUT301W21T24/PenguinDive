package com.cmput301.penguindive;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/*
Not good practice in general, but UI requires specific sequences for testing
This is why all methods begin with a letter, to ensure proper order
https://junit.org/junit4/javadoc/4.12/org/junit/runners/MethodSorters.html
*/

/**
 * This class is to test the MyExperimentActivity
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MyExperimentActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MyExperimentActivity> rule =
            new ActivityTestRule<>(MyExperimentActivity.class, true, true);

    /**
     * Runs before all test and creates a solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the activity
     * @throws Exception
     */
    @Test
    public void aStart() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Ensure the AllExpermentsButton brings us to the MyExperiment Activity
     */
    @Test
    public void bAllExperimentsButton(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnButton("All Experiments");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Adds both a published and unpublished Experiment to the list and makes sure it's been added to the MyExperimentActivity screen
     */
    @Test
    public void cSeeUnpublishedExperiments(){

        // Asserts that the current activity is the MyExperimentActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get floating action button for published
        View fab_pub = solo.getCurrentActivity().findViewById(R.id.add_button);

        solo.clickOnView(fab_pub); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloPublishTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "soloPublishTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editStatus), "publish");
        solo.clickOnButton("OK"); //Select Ok Button

        // Get floating action button for unpublished
        View fab_unpub = solo.getCurrentActivity().findViewById(R.id.add_button);

        solo.clickOnView(fab_unpub); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloUnpublishTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "soloUnpublishTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editStatus), "unpublish");
        solo.clickOnButton("OK"); //Select Ok Button

        // Check to see if we have experiments
        assertTrue(solo.waitForText("soloPublishTest-MyExperiment", 2, 1000));
        assertTrue(solo.waitForText("soloUnpublishTest-MyExperiment", 2, 1000));
    }

    /**
     * Ensure the questions button takes us to QuestionActivity
     */
    @Test
    public void dQuestionButton(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnButton("Questions");
        solo.assertCurrentActivity("Wrong Activity", QuestionActivity.class);

    }
    /**
     * Search the MyExperimentActivity and ensure the proper results are given
     */
    @Test
    public void eSearchExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get searchBar
        View searchBar = solo.getView(R.id.experimentSearchBar); //get the element

        // Search for something that doesn't exist
        searchBar.performClick(); //click on search bar
        solo.enterText(0, "soloTest");

        // Ensure both experiments are not present
        assertFalse((solo.waitForText("soloUnpublishTest-MyExperiment", 2, 1000)));
        assertFalse((solo.waitForText("soloPublishTest-MyExperiment", 2, 1000)));
        solo.clearEditText(0);

        // Search for something that exists
        searchBar.performClick();
        solo.enterText(0, "soloPublishTest-MyExperiment");

        // Ensure published item is present and unpublished is not
        assertTrue(solo.waitForText("soloPublishTest-MyExperiment", 2, 1000)); // Should be results
        assertFalse((solo.waitForText("soloUnpublishTest-MyExperiment", 2, 1000)));
    }

    /**
     * Ensures all created experiments are deleted from MyExperimentActivity
     */
    @Test
    public void fDeleteExperiment(){

        // Make sure previous experiments are there
        assertTrue(solo.waitForText("soloPublishTest-MyExperiment", 2, 1000));
        assertTrue(solo.waitForText("soloUnpublishTest-MyExperiment", 2, 1000));

        // Click on published and delete
        solo.clickOnText("soloPublishTest-MyExperiment");
        solo.clickOnButton("Delete");

        // Click on unpublished and delete
        solo.clickOnText("soloUnpublishTest-MyExperiment");
        solo.clickOnButton("Delete");

        // Make sure deleted
        assertFalse(solo.waitForText("soloPublishTest-MyExperiment", 2, 1000));
        assertFalse(solo.waitForText("soloPublishTest-MyExperiment", 2, 1000));
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
