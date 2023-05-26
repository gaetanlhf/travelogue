package fr.insset.ccm.m1.sag.travelogue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private ProgressBar spinner;
    private Thread networkCheckThread;



    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private String server_client_id;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getSupportActionBar().setTitle(R.string.login_activity_title);
        setContentView(R.layout.activity_login);

        try {
            this.server_client_id = this.getClientSecrets().getDetails().getClientId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.oneTapClient = Identity.getSignInClient(this);
        this.signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(this.server_client_id)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        spinner = findViewById(R.id.login_activity_spinner);
        spinner.setVisibility(View.GONE);
        if (currentUser != null) {
            finish();
        }

        new Thread(() -> {
            NetworkConnectivityCheck.checkConnection(this);
        }).start();

        networkCheckThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000);
                    NetworkConnectivityCheck.checkConnection(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        networkCheckThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "TAG_TOKEN";
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential googleCredential = this.oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = googleCredential.getGoogleIdToken();
                String username = googleCredential.getId();
                String password = googleCredential.getPassword();

                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
                                if(user != null)
                                    finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
//                                updateUI(null);
                            }
                        });
                }
//                else if (password != null) {
//                // Got a saved username and password. Use them to authenticate
//                // with your backend.
//                Log.d(TAG, "Got password.");
//            }

        } catch (ApiException e) {
                // ...
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d(TAG, "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        showOneTapUI = false;
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d(TAG, "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    default:
                        Log.d(TAG, "Couldn't get credential from result."
                                + e.getLocalizedMessage());
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_login, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_account) {
            Intent signUpActivity = new Intent(this, SignUpActivity.class);
            startActivity(signUpActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickLogin(View view) {
        email = findViewById(R.id.login_activity_edittext_email);
        password = findViewById(R.id.login_activity_edittext_password);
        if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
            spinner.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.log_in_success_toast),
                                    Toast.LENGTH_SHORT).show();
                            Intent mainActivity = new Intent(this, MainActivity.class);
                            startActivity(mainActivity);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCheckThread != null) {
            networkCheckThread.interrupt();
        }
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.server_client_secret);

        return GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(inputStream));
    }
}