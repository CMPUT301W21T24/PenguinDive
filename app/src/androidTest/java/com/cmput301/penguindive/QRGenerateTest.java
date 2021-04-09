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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * this class is to test the QRGenerate class
 */
@RunWith(AndroidJUnit4.class)
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
     * gets the activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * checks if the QR code is properly generated for trial results
     */
    @Test
    public void checkGenerateForTrial() {
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
    public void checkGenerateForAd() {
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

        // choose experiment
        solo.clickOnView(exper);
        solo.clickOnView(solo.getView(TextView.class, 0));

        // return to main
        solo.clickOnButton("Generate");
        solo.clickOnButton("Save");
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}

