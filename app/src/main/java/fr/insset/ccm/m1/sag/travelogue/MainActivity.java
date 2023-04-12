package fr.insset.ccm.m1.sag.travelogue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static int autoSaveMomentTimer = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public int getAutoSaveMomentTimer() {
        return MainActivity.autoSaveMomentTimer;
    }

    public static void setAutoSaveMomentTimer(int autoSaveMomentTimer) {
        MainActivity.autoSaveMomentTimer = autoSaveMomentTimer;
    }
}