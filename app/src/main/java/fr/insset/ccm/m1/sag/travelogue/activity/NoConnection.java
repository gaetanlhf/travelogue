package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;

public class NoConnection extends AppCompatActivity {
    private Thread connectivityCheckThread;
    private volatile boolean threadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        threadRunning = true;
        connectivityCheckThread = new Thread(() -> {
            while (threadRunning) {
                if (NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                    Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    break;
                }

                try {
                    Thread.sleep(Constants.TIME_CHECK_CONNECTION_RECONNECT);
                } catch (InterruptedException e) {
                    threadRunning = false;
                }
            }
        });
        connectivityCheckThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!threadRunning) {
            threadRunning = true;
            connectivityCheckThread = new Thread(() -> {
                while (threadRunning) {
                    if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                        Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        break;
                    }

                    try {
                        Thread.sleep(Constants.TIME_CHECK_CONNECTION);
                    } catch (InterruptedException e) {
                        threadRunning = false;
                    }
                }
            });
            connectivityCheckThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
    }
}