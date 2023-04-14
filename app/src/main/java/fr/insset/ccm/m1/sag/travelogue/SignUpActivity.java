package fr.insset.ccm.m1.sag.travelogue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        spinner = (ProgressBar) findViewById(R.id.sign_up_spinner);
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
        email = findViewById(R.id.email_sign_up_text);
        password = findViewById(R.id.password_sign_up_text);
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
}

