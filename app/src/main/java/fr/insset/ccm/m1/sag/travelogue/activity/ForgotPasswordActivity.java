package fr.insset.ccm.m1.sag.travelogue.activity;

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

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView email;

    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.forgot_password_activity_title);
        setContentView(R.layout.activity_forgot_password);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.forgot_password_activity_spinner);
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

    public void onClickResetPassword(View view) {
        email = findViewById(R.id.forgot_password_activity_edittext_email);
        if (!TextUtils.isEmpty(email.getText().toString())) {
            spinner.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.forgot_password_success),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            spinner.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.forgot_password_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}