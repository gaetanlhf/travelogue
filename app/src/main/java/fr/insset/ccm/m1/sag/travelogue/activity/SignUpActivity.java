package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private ProgressBar spinner;
    private Thread networkCheckThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getSupportActionBar().setTitle(R.string.sign_up_activity_title);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.sign_up_activity_spinner);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onClickSignUp(View view) {
        email = findViewById(R.id.sign_up_activity_edittext_email);
        password = findViewById(R.id.sign_up_activity_edittext_password);
        if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
            spinner.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_success_toast),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            spinner.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.sign_up_fail_toast),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCheckThread != null) {
            networkCheckThread.interrupt();
        }
    }
}

