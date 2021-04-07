package com.cmput301.penguindive;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
 * this class is to test the PickQRType class
 */
@RunWith(AndroidJUnit4.class)
public class PickQRTypeTest {

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
     * test that the Advertisement button works
     */
    @Test
    public void checkAdOpen() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");

        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);
        solo.clickOnText("Advertisement");
        solo.assertCurrentActivity("Wrong Activity", QRGenerate.class);
    }

    /**
     * test that the trial result button works
     */
    @Test
    public void checkTrialOpen() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnImage(0);
        solo.clickOnText("Generate QR Code");

        solo.assertCurrentActivity("Wrong Activity", PickQRType.class);
        solo.clickOnText("Trial Result");
        solo.assertCurrentActivity("Wrong Activity", QRGenerate.class);
    }
}
