package com.posa.apps.assignment3;

import com.robotium.solo.Solo;

import junit.framework.Assert;

/**
 * Created by javigm on 3/11/15.
 */
public class Test_DownloadAndRotation extends Test_for_activity3 {

    public void testRun() {

        solo.sleep(shortDelay);
        Assert.assertTrue("Test failed: MainActivity did not load", solo.waitForActivity(MainActivity.class));

        solo.clickOnView(solo.getView(R.id.btn_add_url));

        // Wait for activity
        Assert.assertTrue("Test failed: Button download didnt work",
                solo.waitForText("Please introduce a valid photo URL"));

        solo.sleep(shortDelay);

        solo.clickOnView(solo.getView(R.id.fabDownload));

        solo.sleep(longDelay);
        solo.setActivityOrientation(Solo.LANDSCAPE);

        Assert.assertTrue("Test failed: MainActivity did not load",
                solo.waitForActivity(MainActivity.class));

        // Rotate the screen back to portrait.
        solo.setActivityOrientation(Solo.PORTRAIT);

        // Give the rotation time to settle.
        solo.sleep(longDelay);

        // Wait for activity
        Assert.assertTrue("Test failed: GridViewActivity did not",
                solo.waitForActivity(GridViewActivity.class));

        solo.sleep(longDelay);

        solo.goBackToActivity("MainActivity");

        // Wait for activity
        Assert.assertTrue("Test failed: MainActivity did not",
                solo.waitForActivity(MainActivity.class));

    }

}
