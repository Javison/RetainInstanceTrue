package com.posa.apps.assignment3;

import com.robotium.solo.Solo;

import junit.framework.Assert;

/**
 * Created by javigm on 3/11/15.
 */
public class Test_Basic extends Test_for_activity3 {

    public void testRun() {

        solo.sleep(shortDelay);

        Assert.assertTrue("Test failed: MainActivity did not load",
                solo.waitForActivity(MainActivity.class));

        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(shortDelay);

        Assert.assertTrue("Test failed: MainActivity did not load",
                solo.waitForActivity(MainActivity.class));

        // Rotate the screen back to portrait.
        solo.setActivityOrientation(Solo.PORTRAIT);

        // Give the rotation time to settle.
        solo.sleep(shortDelay);

        // Wait for activity
        Assert.assertTrue("Test failed: MainActivity did not",
                solo.waitForActivity(MainActivity.class));


    }

}
