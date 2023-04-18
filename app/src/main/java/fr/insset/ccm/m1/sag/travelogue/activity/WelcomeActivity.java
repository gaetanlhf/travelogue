package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.insset.ccm.m1.sag.travelogue.R;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
    }

    public void onClickStart(View view) {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }
}