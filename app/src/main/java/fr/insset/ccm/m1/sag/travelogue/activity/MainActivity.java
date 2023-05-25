package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.db.InitDatabase;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_main);
        new Thread(() -> {
            NetworkConnectivityCheck.checkConnection(this);
        }).start();
        AppSettings.setup(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            InitDatabase initDatabase = new InitDatabase(mAuth.getCurrentUser().getUid());
            initDatabase.isInit(init -> {
                if (!init.get()) {
                    initDatabase.initDb();
                }
                Settings settings = new Settings(mAuth.getCurrentUser().getUid());
                settings.getSettings(atomicReferenceArray -> {
                    AppSettings.setTimeBetweenAutoGps(Long.parseLong(atomicReferenceArray.get(1).toString()));
                    AppSettings.setAutoGps(Boolean.parseBoolean(atomicReferenceArray.get(0).toString()));
                });
                State state = new State(mAuth.getCurrentUser().getUid());
                state.isTravelling(travelling -> {
                    if (travelling.get()) {
                        AppSettings.setTravelling(travelling.get());
                        state.getCurrentTravel(currentTravel -> {
                            AppSettings.setTravel(currentTravel.get());
                            if (!LocationService.isServiceRunning && AppSettings.getTravelling() && AppSettings.getAutoGps()) {
                                if (!PermissionsHelper.hasPermission(MainActivity.this, Constants.ACCESS_FINE_LOCATION_PERMISSION)) {
                                    PermissionsHelper.requestPermissions(MainActivity.this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
                                }
                                Intent intent = new Intent(MainActivity.this, LocationService.class);
                                intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                                intent.putExtra("timeBetweenUpdate", AppSettings.getTimeBetweenAutoGps());
                                MainActivity.this.startService(intent);
                            }
                        });
                    } else {
                        AppSettings.setTravelling(false);
                    }
                });
                File folder = new File(getCacheDir(), "/export/");
                if (!folder.exists()) {
                    if (folder.mkdir()) {
                        folder.mkdirs();
                    }
                }
                Intent homeActivity = new Intent(this, HomeActivity.class);
                overridePendingTransition(0, 0);
                startActivity(homeActivity);
                finish();
            });
        } else {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            overridePendingTransition(0, 0);
            startActivity(welcomeActivity);
            finish();
        }

    }
}