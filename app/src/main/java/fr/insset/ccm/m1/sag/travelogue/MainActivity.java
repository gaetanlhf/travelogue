package fr.insset.ccm.m1.sag.travelogue;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.insset.ccm.m1.sag.travelogue.db.InitDatabase;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        InitDatabase initDatabase = new InitDatabase(mAuth.getCurrentUser().getUid());
        initDatabase.isInit(init -> {
            if (init.get()) {
                initDatabase.initDb();
            }
            Intent homeActivity = new Intent(this, HomeActivity.class);
            startActivity(homeActivity);
            finish();
        });

    }
}