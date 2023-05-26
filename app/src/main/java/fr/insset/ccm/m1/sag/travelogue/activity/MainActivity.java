package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.material.elevation.SurfaceColors;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.InitDatabase;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPrefManager sharedPrefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_main);

        new Thread(() -> {
            NetworkConnectivityCheck.checkConnection(this);
        }).start();

        sharedPrefManager = SharedPrefManager.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            InitDatabase initDatabase = new InitDatabase(mAuth.getCurrentUser().getUid());
            initDatabase.isInit(init -> {
                if (!init.get()) {
                    initDatabase.initDb();
                }
                Settings settings = new Settings(mAuth.getCurrentUser().getUid());
                settings.getSettings(atomicReferenceArray -> {
                    sharedPrefManager.saveLong("TimeBetweenAutoGps", Long.parseLong(atomicReferenceArray.get(1).toString()));
                    sharedPrefManager.saveBool("AutoGps", Boolean.parseBoolean(atomicReferenceArray.get(0).toString()));
                });
                State state = new State(mAuth.getCurrentUser().getUid());
                state.isTravelling(travelling -> {
                    if (travelling.get()) {
                        sharedPrefManager.saveBool("Travelling", travelling.get());
                        state.getCurrentTravel(currentTravel -> {
                            sharedPrefManager.saveString("CurrentTravel", currentTravel.get());
                            Log.d("test", sharedPrefManager.getString("CurrentTravel"));
                            if (!LocationService.isServiceRunning && sharedPrefManager.getBool("Travelling") && sharedPrefManager.getBool("AutoGps")) {
                                if (!PermissionsHelper.hasPermission(MainActivity.this, Constants.ACCESS_FINE_LOCATION_PERMISSION)) {
                                    PermissionsHelper.requestPermissions(MainActivity.this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
                                }
                                Intent intent = new Intent(MainActivity.this, LocationService.class);
                                intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                                intent.putExtra("timeBetweenUpdate", sharedPrefManager.getLong("TimeBetweenAutoGps"));
                                MainActivity.this.startService(intent);
                            }
                        });
                    } else {
                        sharedPrefManager.saveBool("Travelling", false);
                        sharedPrefManager.saveString("CurrentTravel", null);
                    }
                });
                File folder = new File(getCacheDir(), "/export/");
                if (!folder.exists()) {
                    if (folder.mkdir()) {
                        Boolean isCreated = folder.mkdirs();
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