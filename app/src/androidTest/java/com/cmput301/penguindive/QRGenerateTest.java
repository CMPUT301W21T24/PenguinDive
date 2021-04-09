package com.cmput301.penguindive;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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

/**
 * this class is to test the QRGenerate class
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QRGenerateTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
        new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * This function runs before all test and creates a solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Add a published Experiment to the list to ensure no other tests fail
     */
    @Test
    public void aAddPublishedExperiment(){

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Get floating action button
        View fab = solo.getCurrentActivity().findViewById(R.id.add_button);
        solo.clickOnView(fab); //Click Add experiment Button

        //Get view for EditText and enter a experiment name, description and status
        solo.enterText((EditText) solo.getView(R.id.editTitle), "AScanTestPublished");
        solo.enterText((EditText) solo.getView(R.id.editDescription), "DescTest");
        solo.enterText((EditText) solo.getView(R.id.editRegion), "RegionTest");
        solo.pressSpinnerItem(0, 0); // Choose published
        solo.clickOnButton("OK"); //Select Ok Button
    }
    /**
     * gets the activity
     * @throws Exception
     */
    @Test
    public void bStart() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if the QR code is properly generated for trial results
     */
    @Test
    public void bCheckGenerateForTrial() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        View v = solo.getView(ListView.class, 0);
        solo.clickOnView(v);
        if (solo.waitForText("Subscribe Confirmation")) {
            solo.clickOnText("OK");
        } else if (solo.waitForText("Unsubscribe Confirmation")) {
            solo.clickOnText("Cancel");
        }

        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");

        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);
        solo.clickOnButton("Trial Result");
        solo.assertCurrentActivity("Wrong Activity", QRGenerate.class);

        // get spinners
        View exper = solo.getView(Spinner.class, 0);
        View pf = solo.getView(Spinner.class, 1);

        // choose experiment
        solo.clickOnView(exper);
        solo.clickOnView(solo.getView(TextView.class, 0));

        //choose pass or fail
        solo.clickOnView(pf);
        solo.clickOnView(solo.getView(TextView.class, 1));

        // return to main
        solo.clickOnButton("Generate");
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Test for generating a QR code for advertisements
     */
    @Test
    public void bCheckGenerateForAd() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        View v = solo.getView(ListView.class, 0);
        solo.clickOnView(v);
        if (solo.waitForText("Subscribe Confirmation")) {
            solo.clickOnText("OK");
        } else if (solo.waitForText("Unsubscribe Confirmation")) {
            solo.clickOnText("Cancel");
        }

        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");

        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);
        solo.clickOnButton("Advertisement");
        solo.assertCurrentActivity("Wrong Activity", QRGenerate.class);

        // get spinners
        View exper = solo.getView(Spinner.class, 0);

        // choose experiment
        solo.clickOnView(exper);
        solo.clickOnView(solo.getView(TextView.class, 0));

        // return to main
        solo.clickOnButton("Generate");
        solo.clickOnButton("Save");
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Ensures published experiment is deleted from MainActivity
     */
    @Test
    public void dDeleteExperiment() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // Go to My Experiments
        solo.clickOnImage(0);
        solo.clickOnText("My Experiments");

        // Click on published and delete
        solo.scrollToTop();
        solo.clickOnText("AScanTestPublished", 1, true);
        solo.clickOnButton("Delete");
    }
}

