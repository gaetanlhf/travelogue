package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;

public class NoConnection extends AppCompatActivity {
    private Thread networkCheckThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        networkCheckThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    NetworkConnectivityCheck.checkConnection(isConnected -> {
                        if (isConnected.get()) {
                            Intent intent = new Intent(this, MainActivity.class);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            finish();
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        networkCheckThread.start();
        setContentView(R.layout.activity_no_connection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCheckThread != null) {
            networkCheckThread.interrupt();
        }
    }
}