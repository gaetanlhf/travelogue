package fr.insset.ccm.m1.sag.travelogue.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Users;

public class LoginActivity extends AppCompatActivity {

    private static final String writingPhotoScope = "https://www.googleapis.com/auth/photoslibrary.appendonly";
    private static final String readingOnlyPhotosCreatedPhotoByTravelogueScope = "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata";
    private static final String editingOnlyPhotosCreatedPhotoByTravelogueScope = "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata";
    private static final String sharingPhotoScope = "https://www.googleapis.com/auth/photoslibrary.sharing";
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String server_client_id;
    private String authCode;
    private final Users users = new Users();

    public static Thread googleApiClientThread;

    private String accessToken;
    private String refreshToken;
    private Long expiresInSeconds;
    private final ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    SharedMethods.displayToast(getApplicationContext(), getString(R.string.unable_to_sign_in_with_google_error_text));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getSupportActionBar().setTitle(R.string.login_activity_title);
        setContentView(R.layout.activity_login);

        signInButton = findViewById(R.id.sign_in_with_google_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        customizeGooglePlusButton(signInButton);

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

        signInButton.setOnClickListener(view -> {
            // Initialize sign in intent
            Intent intent = mGoogleSignInClient.getSignInIntent();
            // Start activity for result
            activityResultLaunch.launch(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        spinner = findViewById(R.id.login_activity_spinner);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void customizeGooglePlusButton(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(R.string.sign_in_with_google_button_text);
                return;
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            this.authCode = account.getServerAuthCode();

            // When sign in account is not equal to null initialize auth credential
            AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            // Check credential
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // When task is successful redirect to profile activity display Toast
                        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        SharedMethods.displayToast(getApplicationContext(), getString(R.string.successful_sign_in_with_google));
                        finish();
                    }
                } else {
                    // When task is unsuccessful display Toast
                    SharedMethods.displayToast(
                            getApplicationContext(),
                            "Authentication Failed :" + Objects.requireNonNull(task.getException()).getMessage()
                    );
                }
            });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            SharedMethods.displayToast(
                    getApplicationContext(),
                    "signInResult:failed code=" + e.getStatusCode()
            );
        }
    }
}
