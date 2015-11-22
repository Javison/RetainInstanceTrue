package com.posa.apps.assignment3;

import android.util.Log;

/**
 * Created by javigm on 3/11/15.
 */
public class Alog {
    public static String debugTag = "MOOC DEB";
    public static String warningTag = "MOOC WAR";
    public static String errorTag = "MOOC ERR";

    public static boolean debugging = true;
    public static boolean warning = true;
    public static boolean error = true;

    public static void debug(String classOrigin, String s){
        if(debugging){
            Log.i(debugTag, classOrigin + ": " + s);
        }
    }

    public static void warning(String classOrigin, String s){
        if(debugging || warning){
            Log.i(warningTag, classOrigin + ": " + s);
        }
    }

    public static void error(String classOrigin, String s){
        if(debugging || warning || error){
            Log.i(errorTag, classOrigin + ": " + s);
        }

    }


}
