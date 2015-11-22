package com.posa.apps.assignment3;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by javigm on 3/11/15.
 */
public class Test_for_activity3 extends ActivityInstrumentationTestCase2<MainActivity> {

    public Test_for_activity3(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    /**
     * Some useful delay/timer values.
     */
    public static int MILLISECOND = 100;
    public static int shortDelay = 2 * MILLISECOND;
    public static int mediumDelay = 5 * MILLISECOND;
    public static int longDelay = 10 * MILLISECOND;
    public static int veryLongDelay = 30 * MILLISECOND;
    protected Solo solo;

    public Test_for_activity3() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp()throws Exception {
        super.setUp();
        solo=new Solo(getInstrumentation(),getActivity());
    }
}
