package com.cmput301.penguindive;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.drawerlayout.widget.DrawerLayout;
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
import org.w3c.dom.Text;

import java.util.regex.Pattern;

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
     * Ensure the navigation drawer brings us to the MainActivity Activity
     */
    @Test
    public void aNavHome(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Home");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Ensure the navigation drawer brings us to the MyExperiment Activity
     */
    @Test
    public void aNavMyExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("My Experiments");
        solo.assertCurrentActivity("Wrong Activity", MyExperimentActivity.class);
    }

    /**
     * Ensure the navigation drawer brings us to the Profile Activity
     */
    @Test
    public void aNavMyProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("My Profile");
        solo.assertCurrentActivity("Wrong Activity", Profile.class);
    }

    /**
     * Ensure the navigation drawer brings us to the SearchProfile Activity
     */
    @Test
    public void aNavSearchProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Search Users");
        solo.assertCurrentActivity("Wrong Activity", SearchProfile.class);
    }

    /**
     * Ensure the navigation drawer brings us to the PickScanType Activity
     */
    @Test
    public void aNavPickScanType(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Scan QR Code");
        solo.assertCurrentActivity("Wrong Activity", PickScanType.class);
    }

    /**
     * Ensure the navigation drawer brings us to the PickQRType Activity
     */
    @Test
    public void aNavPickQRType(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");
        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);

    }

    /**
     * Ensure the refresh button keeps us on the MainActivity Activity
     */
    @Test
    public void aToolbarRefresh(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(2);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Add a published Experiment to the list and make sure it's been added to the screen
     */
    @Test
    public void bAddPublishedExperiment(){

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloPublishedTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 0); // Choose published
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's in list
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
    }


    /**
     * Add an unpublished Experiment to the list and make sure it has not been added to the screen
     */
    @Test
    public void bAddUnpublishedExperiment(){

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloUnpublishedTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 1); // Choose unpublished
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's not in list
        assertFalse("Ended not present", solo.waitForText("soloUnpublishedTest-Main", 1 , 2000, true, true));
    }

    /**
     * Add an ended experiment to the list and make sure it has been added to the screen
     */

    @Test
    public void bAddEndedExperiment(){

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "soloEndedTest-Main");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 2); // Choose ended
        solo.clickOnButton("OK"); //Select Ok Button

        // Make sure it's in list
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
    }


    /**
     * Search the main activity and ensure the proper results are given
     */
    @Test
    public void cSearchExperiment(){

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get search bar
        View searchBar = solo.getView(RelativeLayout.class, 1);

        // Search by title

            // Search for something that is unpublished
        solo.clickOnView(searchBar);  // Click on search bar
        solo.enterText(0, "soloUnpublishedTest");

            // Ensure published item is not present
        assertFalse("Published not present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.clearEditText(0);

            // Search for something that exists
        solo.clickOnView(searchBar);
        solo.enterText(0, "soloPublishedTest");

            // Ensure published item is present
        solo.scrollToTop();
        assertTrue("Published not present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.scrollToTop();
        assertFalse("Ended is present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));

        solo.clearEditText(0);

        // Search by Description
        solo.clickOnView(searchBar);
        solo.enterText(0, "DescTest");

            // Ensure published and ended items are present
        solo.scrollToTop();
        assertTrue("Published not present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true,true ));
        solo.scrollToTop();
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
        solo.clearEditText(0);

        // Search by Status
        solo.clickOnView(searchBar);
        solo.enterText(0, "Ended");

            // Ensure ended items are present and published are not
        solo.scrollToTop();
        assertFalse("Published is present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
        solo.clearEditText(0);


        // Search by Region
        solo.clickOnView(searchBar);
        solo.enterText(0, "RegionTest");

            // Ensure Published/Ended items are present
        solo.scrollToTop();
        assertTrue("Published not present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
        solo.clearEditText(0);

        // Search by Minimum Trials
        solo.clickOnView(searchBar);
        solo.enterText(0, "1");

            // Ensure published/ended items are present
        solo.scrollToTop();
        assertTrue("Published not present",solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.scrollToTop();
        assertTrue("Ended not present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));

    }

    /**
     * Ensures published and ended experiments are deleted from MainActivity
     */
    @Test
    public void dDeleteExperiment(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Go to My Experiments
        solo.clickOnImage(0);
        solo.clickOnText("My Experiments");

        // Click on published and delete
        solo.clickOnText("soloPublishedTest-Main");
        solo.clickOnButton("Delete");

        // Click on ended and delete
        solo.clickOnText("soloEndedTest-Main");
        solo.clickOnButton("Delete");

        // Click on unpublished and delete
        solo.clickOnText("soloUnpublishedTest-Main");
        solo.clickOnButton("Delete");

        // Go back to MainActivity
        solo.clickOnImage(0);
        solo.clickOnText("Home");

        // Ensure published/ended experiments are gone
        assertFalse("Publish still present", solo.waitForText("soloPublishedTest-Main", 1 , 2000, true, true));
        solo.scrollToTop();
        assertFalse("Ended still present", solo.waitForText("soloEndedTest-Main", 1 , 2000, true, true));
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
