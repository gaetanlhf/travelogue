package fr.insset.ccm.m1.sag.travelogue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int ALL_PERMISSION_CODE = 100;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String ACCESS_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //todo
        }
        
    }

    public void checkPermission()
    {
        if(ContextCompat.checkSelfPermission(
                MainActivity.this, ACCESS_FINE_LOCATION_PERMISSION)
                + ContextCompat.checkSelfPermission(
                MainActivity.this, ACCESS_COARSE_LOCATION_PERMISSION)
                + ContextCompat.checkSelfPermission(
                MainActivity.this, CAMERA_PERMISSION)
                != PackageManager.PERMISSION_GRANTED){
            Log.d("PERMISSION", "Request permissions");

            ActivityCompat.requestPermissions(MainActivity.this, new String[] { CAMERA_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION }, ALL_PERMISSION_CODE);

        }
        else {
            Log.d("PERMISSION", "Permissions already granted");

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //todo
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //todo
        }
    }

    public void onClickStart(View view) {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        overridePendingTransition(0, 0);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSION_CODE) {
            if (grantResults.length > 0) {

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (cameraAccepted)
                    Toast.makeText(this, "Permission Granted, Now you can access camera.", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

                boolean fineLocationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (fineLocationAccepted)
                    Toast.makeText(this, "Permission Granted, Now you can access location.", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}