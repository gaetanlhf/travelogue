package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.InitDatabase;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_main);
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
                    AppSettings.setTimeBetweenAutoGps(Integer.parseInt(atomicReferenceArray.get(1).toString()));
                    AppSettings.setAutoGps(Boolean.parseBoolean(atomicReferenceArray.get(0).toString()));
                });
                File folder = new File(getCacheDir(), "/export/");
                if (!folder.exists()) {
                    if (folder.mkdir()) {
                        folder.mkdirs();
                    }
                }
                Intent homeActivity = new Intent(this, HomeActivity.class);
                startActivity(homeActivity);
                finish();
            });
        } else {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeActivity);
            finish();
        }
    }
}