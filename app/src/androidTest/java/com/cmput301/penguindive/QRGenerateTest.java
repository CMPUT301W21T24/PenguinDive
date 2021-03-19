package com.cmput301.penguindive;

import android.app.Activity;
import android.widget.EditText;

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
    public ActivityTestRule<QRGenerate> rule =
        new ActivityTestRule<>(QRGenerate.class, true, true);

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
     * checks if the QR code is properly generated
     */
    @Test
    public void checkGenerate() {
        solo.assertCurrentActivity("Wrong Activity", QRGenerate.class);

        solo.enterText((EditText) solo.getView(R.id.experiment_name), "test");
        solo.clickOnButton("Generate");
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}

