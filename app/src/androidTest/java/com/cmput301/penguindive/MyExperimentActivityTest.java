package com.cmput301.penguindive;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;


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


import java.util.regex.Pattern;

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
     * Ensure the navigation drawer brings us to the MyExperimentActivity Activity
     */
    @Test
    public void aNavHome(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Home");
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
    }

    /**
     * Ensure the navigation drawer brings us to the MyExperiment Activity
     */
    @Test
    public void aNavMyExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("My Experiments");
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
    }

    /**
     * Ensure the navigation drawer brings us to the Profile Activity
     */
    @Test
    public void aNavMyProfile(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("My Profile");
        solo.assertCurrentActivity("Wrong Activity", Profile.class);
    }

    /**
     * Ensure the navigation drawer brings us to the SearchProfile Activity
     */
    @Test
    public void aNavSearchProfile(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Search Users");
        solo.assertCurrentActivity("Wrong Activity", SearchProfile.class);
    }

    /**
     * Ensure the navigation drawer brings us to the PickScanType Activity
     */
    @Test
    public void aNavPickScanType(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Scan QR Code");
        solo.assertCurrentActivity("Wrong Activity", PickScanType.class);
    }

    /**
     * Ensure the navigation drawer brings us to the PickQRType Activity
     */
    @Test
    public void aNavPickQRType(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");
        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);

    }

    /**
     * Ensure the refresh button keeps us on the MyExperimentActivity Activity
     */
    @Test
    public void aToolbarRefresh(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
        solo.clickOnImage(2);
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
    }

    /**
     * Add a published Experiment to the list and make sure it's been added to the screen
     */
    @Test
    public void bAddPublishedExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloPublishedTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 0); // Choose published
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's in list
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
    }


    /**
     * Add an unpublished Experiment to the list and make sure it has been added to the screen
     */
    @Test
    public void bAddUnpublishedExperiment(){

        // Asserts that the current activity is the MyExperimentActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloUnpublishedTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 1); // Choose unpublished
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's in list
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
    }

    /**
     * Add an ended experiment to the list and make sure it has been added to the screen
     */

    @Test
    public void bAddEndedExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloEndedTest-MyExperiment");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 2); // Choose ended
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's in list
        assertTrue("Ended is not present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
    }


    /**
     * Search the MyExperiment activity and ensure the proper results are given
     */
    @Test
    public void cSearchExperiment(){

        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Get search bar
        View searchBar = solo.getView(RelativeLayout.class, 1);

        // Search by title

            // Search for something that is not our new experiments
        solo.clickOnView(searchBar);  // Click on search bar
        solo.enterText(0, "soloNotpublishedTest");

            // Ensure all items are not present
        assertFalse("Published is present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertFalse("Ended is present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertFalse("Unpublished is present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
        solo.clearEditText(0);

            // Search for something that exists
        solo.clickOnView(searchBar);
        solo.enterText(0, "soloPublishedTest");

            // Ensure published item is present
        solo.scrollToTop();
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.clearEditText(0);

        // Search by Description
        solo.clickOnView(searchBar);
        solo.enterText(0, "DescTest");

            // Ensure all items are present
        solo.scrollToTop();
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended is not present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
        solo.clearEditText(0);

        // Search by Status

            // Ensure unpublished items are present and nothing else
        solo.clickOnView(searchBar);
        solo.enterText(0, "Unpublished");

        solo.scrollToTop();
        assertFalse("Published is present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertFalse("Ended is present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
        solo.clearEditText(0);

        // Search by Region
        solo.clickOnView(searchBar);
        solo.enterText(0, "RegionTest");

            // Ensure all items are present
        solo.scrollToTop();
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended is not present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
        solo.clearEditText(0);

//         Search by Minimum Trials
        solo.clickOnView(searchBar);
        solo.enterText(0, "1");

            // Ensure all items are present
        solo.scrollToTop();
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended is not present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));

    }

    /**
     * Ensures all experiments are deleted from MyExperimentActivity
     */
    @Test
    public void dDeleteExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);

        // Make sure experiments are there
        assertTrue("Published is not present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended is not present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertTrue("Unpublished is not present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));

        // Click on published and delete
        solo.clickOnText("soloPublishedTest-MyExperiment");
        solo.clickOnButton("Delete");

        // Click on ended and delete
        solo.clickOnText("soloEndedTest-MyExperiment");
        solo.clickOnButton("Delete");

        // Click on unpublished and delete
        solo.clickOnText("soloUnpublishedTest-MyExperiment");
        solo.clickOnButton("Delete");

        // Ensure all experiments are gone
        assertFalse("Published is present", solo.waitForText("soloPublishedTest", 1 , 2000, true, true));
        solo.scrollToTop();
        assertFalse("Ended is present", solo.waitForText("soloEndedTest-MyExperiment", 1, 2000, true, true));
        solo.scrollToTop();
        assertFalse("Unpublished is present", solo.waitForText("soloUnpublishedTest-MyExperiment", 1, 2000, true, true));
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
