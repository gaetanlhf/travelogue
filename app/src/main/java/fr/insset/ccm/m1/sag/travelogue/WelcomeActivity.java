package fr.insset.ccm.m1.sag.travelogue;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class WelcomeActivity extends AppCompatActivity {

    private static final int ALL_PERMISSION_CODE = 100;

    private static final int LOCATION_PERMISSION_CODE = 101;
    private static final String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;
    private static final String ACCESS_COARSE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btn_start_location_service).setOnClickListener(view -> startLocationService());
        findViewById(R.id.btn_stop_location_service).setOnClickListener(view -> stopLocationService());

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


    private boolean isLocationServiceRunning(){

        return LocationService.isServiceRunning;

        /*
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;*/
    }


    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            Log.d("intent", intent.getAction());
            startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void getLocation(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION}, LOCATION_PERMISSION_CODE);
        }else{
            startLocationService();
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(
                WelcomeActivity.this, ACCESS_FINE_LOCATION_PERMISSION)
                + ContextCompat.checkSelfPermission(
                WelcomeActivity.this, ACCESS_COARSE_LOCATION_PERMISSION)
                + ContextCompat.checkSelfPermission(
                WelcomeActivity.this, CAMERA_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Request permissions");

            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{CAMERA_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION}, ALL_PERMISSION_CODE);

        } else {
            Log.d("PERMISSION", "Permissions already granted");

        }
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