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
 * This class is to test the MainActivity
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

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
     * Ensure the myExpermentButton brings us to the MyExperiment Activity
     */
    @Test
    public void bMyExperimentButton(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("My Experiments");
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
    }

    /**
     * Add a published Experiment to the list and make sure it's been added to the screen
     */
    @Test
    public void cAddPublishedExperiment(){

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloPublishTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "soloPublishTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editStatus), "publish");
        solo.clickOnButton("OK"); //Select Ok Button

        // True if the experiment has been added (title and description should be present)
        assertTrue(solo.waitForText("soloPublishTest-Main", 2, 1000));
    }

    /**
     * Add an unpublished Experiment to the list and make sure it has not been added to the screen
     */
    @Test
    public void dAddUnpublishedExperiment(){
        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloUnpublishTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "soloUnpublishTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editStatus), "unpublish");
        solo.clickOnButton("OK"); //Select Ok Button

        //False if there is no unpublished experiment on the main activity
        assertFalse(solo.searchText("soloUnpublishTest-Main"));
    }

    /**
     * Search the main activity and ensure the proper results are given
     */
    @Test
    public void eSearchExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get searchBar
        View searchBar = solo.getView(R.id.experimentSearchBar); //get the element

        // Search for something that doesn't exist
        searchBar.performClick(); //click on search bar
        solo.enterText(0, "soloUnPublishTest-Main");
        // Ensure published item is not present
        assertFalse((solo.waitForText("soloPublishTest-Main", 2, 1000))); // Should be no results returned
        solo.clearEditText(0);

        // Search for something that exists
        searchBar.performClick();
        solo.enterText(0, "soloPublishTest-Main");
        // Ensure published item is present
        assertTrue(solo.waitForText("soloPublishTest-Main", 2, 1000)); // Should be results
    }

    /**
     * Ensures experiments are deleted from MainActivity (published)
     */
    @Test
    public void fDeleteExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Go to my experiments
        solo.clickOnButton("My Experiments");

        // Make sure previous experiments are there
        assertTrue(solo.waitForText("soloPublishTest-Main", 2, 1000));
        assertTrue(solo.waitForText("soloUnpublishTest-Main", 2, 1000));

        // Click on published and delete
        solo.clickOnText("soloPublishTest-Main");
        solo.clickOnButton("Delete");

        // Click on unpublished and delete
        solo.clickOnText("soloUnpublishTest-Main");
        solo.clickOnButton("Delete");

        // Make sure deleted in MainActivity
        solo.clickOnButton("All Experiments");
        assertFalse(solo.waitForText("soloPublishTest-Main", 2, 1000));
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
