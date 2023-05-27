package fr.insset.ccm.m1.sag.travelogue.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.elevation.SurfaceColors;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private ProgressBar spinner;
    private Thread networkCheckThread;

    private SignInButton signUpGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String server_client_id;
    private String authCode;

    private final ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    int tt = result.getResultCode();
                    displayToast(String.valueOf(tt));
                }
            });

    private static final String writingPhotoScope = "https://www.googleapis.com/auth/photoslibrary.appendonly";
    private static final String readingOnlyPhotosCreatedPhotoByTravelogueScope = "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata";
    private static final String editingOnlyPhotosCreatedPhotoByTravelogueScope = "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata";
    private static final String sharingPhotoScope = "https://www.googleapis.com/auth/photoslibrary.sharing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getSupportActionBar().setTitle(R.string.sign_up_activity_title);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signUpGoogleButton = findViewById(R.id.sign_in_with_google_button);
        signUpGoogleButton.setSize(SignInButton.SIZE_STANDARD);
        signUpGoogleButton.setColorScheme(SignInButton.COLOR_DARK);
        customizeGooglePlusButton(signUpGoogleButton);

        server_client_id = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(
                        new Scope(writingPhotoScope),
                        new Scope(readingOnlyPhotosCreatedPhotoByTravelogueScope),
                        new Scope(editingOnlyPhotosCreatedPhotoByTravelogueScope))
                .requestServerAuthCode(server_client_id)
                .requestIdToken(server_client_id)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signUpGoogleButton.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = mGoogleSignInClient.getSignInIntent();
            // Start activity for result
            activityResultLaunch.launch(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.sign_up_activity_spinner);
        spinner.setVisibility(View.GONE);

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

    private GoogleClientSecrets getClientSecrets() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.server_client_secret);
        return GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(), new InputStreamReader(inputStream));
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void customizeGooglePlusButton(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(R.string.sign_up_with_google_button_text);
                return;
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            this.authCode = account.getServerAuthCode();
            displayToast("Google sign is successful");

            // When sign in account is not equal to null initialize auth credential
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            // Check credential

//            SignInCredential googleCredential = GoogleAuthCredential.
//            String idToken = googleCredential.getGoogleIdToken();
//            String username = googleCredential.getId();
//            String password = googleCredential.getPassword();

            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user != null) {
                        // When task is successful redirect to profile activity display Toast
                        startActivity(new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        displayToast("Firebase authentication successful");
                        finish();
                    }
                } else {
                    // When task is unsuccessful display Toast
                    displayToast("Authentication Failed :" + Objects.requireNonNull(task.getException()).getMessage());
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            String s = "signInResult:failed code=" + e.getStatusCode();
            displayToast(s);
        }
    }
}

