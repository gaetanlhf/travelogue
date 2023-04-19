package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.insset.ccm.m1.sag.travelogue.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        spinner = findViewById(R.id.log_in_spinner);
        spinner.setVisibility(View.GONE);
        if (currentUser != null) {
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
        }
    }

    public void onClickCreateAccountActivity(View view) {
        Intent signUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(signUpActivity);
    }

    public void onClickLogin(View view) {
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);
        if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
            spinner.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.log_in_success_toast),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            spinner.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.log_in_fail_toast),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void onClickForgotPassword(View view) {
        Intent forgotPasswordActivity = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordActivity);
    }
}