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
    public ActivityTestRule<PickScanType> rule =
            new ActivityTestRule<>(PickScanType.class, true, true);

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
     * tests that the QRScanner class works when register barcode chosen
     */
    @Test
    public void checkBackBarReg() {
        solo.assertCurrentActivity("Wrong Activity", PickScanType.class);
        solo.clickOnButton("Register BarCode");
        solo.assertCurrentActivity("Wrong Activity", QRScanner.class);
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * tests that the QRScanner class works when scan barcode chosen
     */
    @Test
    public void checkBackBarScan() {
        solo.assertCurrentActivity("Wrong Activity", PickScanType.class);
        solo.clickOnButton("barcode");
        solo.assertCurrentActivity("Wrong Activity", QRScanner.class);
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * tests that the QRScanner class works when scan QR code chosen
     */
    @Test
    public void checkBackQRScan() {
        solo.assertCurrentActivity("Wrong Activity", PickScanType.class);
        solo.clickOnButton("QR Code");
        solo.assertCurrentActivity("Wrong Activity", QRScanner.class);
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}
