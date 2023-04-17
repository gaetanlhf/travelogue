package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        setContentView(R.layout.activity_main);
        AppSettings.setup(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            launchMainUi();
        } else {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            startActivity(welcomeActivity);
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            launchMainUi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            launchMainUi();
        }
    }


    public void launchMainUi() {
        InitDatabase initDatabase = new InitDatabase(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        initDatabase.isInit(init -> {
            if (init.get()) {
                initDatabase.initDb();
            }
            Settings settings = new Settings(mAuth.getCurrentUser().getUid());
            settings.getSettings(atomicReferenceArray -> {
                AppSettings.setTimeBetweenAutoGps(Integer.parseInt(atomicReferenceArray.get(1).toString()));
                AppSettings.setAutoGps(Boolean.parseBoolean(atomicReferenceArray.get(0).toString()));
            });
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
            overridePendingTransition(0, 0);
            finish();
        });

    }


}