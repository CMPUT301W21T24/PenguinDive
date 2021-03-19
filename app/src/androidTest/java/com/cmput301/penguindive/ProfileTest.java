package com.cmput301.penguindive;


import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Profile> rule = new ActivityTestRule<>(Profile.class, true, true);

    /**
     * Runs before all test and creates a solo instance
     *
     * @throws Exception
     */

    @Before
    public void setup() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Runs before all test and creates a solo instance
     *
     * @throws Exception
     */

    @Test
    public void aStart() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Ensure the ProfileButton brings us to the Profile Activity
     */

    @Test
    public void bProfileButton() {
        solo.assertCurrentActivity("Wrong Activity", Profile.class);
        solo.clickOnButton("Search Profiles");
        solo.assertCurrentActivity("Wrong Activity", SearchProfile.class );
    }

    @Test
    public void onBackPressed() {
        solo.assertCurrentActivity("Wrong Activity",Profile.class);
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);
    }
}

