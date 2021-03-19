package com.cmput301.penguindive;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the QRScanner class
 */
public class QRScannerTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<QRScanner> rule =
            new ActivityTestRule<>(QRScanner.class, true, true);

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
     * tests that the QRScanner class works
     */
    @Test
    public void checkBack() {
        solo.assertCurrentActivity("Wrong Activity", QRScanner.class);
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}
