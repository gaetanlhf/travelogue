package fr.insset.ccm.m1.sag.travelogue.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SharedMethods {
    public static void displayToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void displayLogMessage(int priority, String tag, String message) {
        Log.println(priority, tag, message);
    }

    public static void displayDebugLogMessage(String tag, String message) {
        Log.d(tag, message);
    }
}
