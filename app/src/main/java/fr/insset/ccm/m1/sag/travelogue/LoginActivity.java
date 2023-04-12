package fr.insset.ccm.m1.sag.travelogue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        overridePendingTransition(0, 0);
    }

    public void onClickLogin(View view) {
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.log_in_success_toast),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.log_in_fail_toast),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}