package fr.insset.ccm.m1.sag.travelogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Settings;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class NewTravelActivity extends AppCompatActivity {

    private TextView travelName;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_new_travel));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_new_travel);
        if(!PermissionsHelper.hasPermissions(this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION})){
            PermissionsHelper.requestPermissions(this, new String[]{Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
        }
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

    public void onClickSaveTravel(View view) {
        travelName = findViewById(R.id.activity_new_travel_edittext);
        if (!TextUtils.isEmpty(travelName.getText().toString())) {
            TravelHelper newTravel = new TravelHelper(mAuth.getCurrentUser().getUid());
            newTravel.createTravel(travelName.getText().toString());
            Settings settings = new Settings(mAuth.getCurrentUser().getUid());
            settings.isPeriodicTrackingEnable(data -> {
                if(data.get(0).equals("true")){
                    Log.d("TEST", data.get(1).toString());
                    startLocationService(Long.parseLong(data.get(1).toString()));
                }
            });
            finish();
        }
    }

    private void startLocationService(Long timeBetweenAutoGetPoint){
        if(!LocationService.isServiceRunning){
            if(!PermissionsHelper.hasPermission(this, Constants.ACCESS_FINE_LOCATION_PERMISSION)){
                PermissionsHelper.requestPermissions(this, new String[] {Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION, Constants.ACCESS_COARSE_LOCATION_PERMISSION, Constants.ACCESS_FINE_LOCATION_PERMISSION, Constants.FOREGROUND_SERVICE_PERMISSION}, Constants.LOCATION_PERMISSION_CODE);
            }
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            intent.putExtra("timeBetweenUpdate", timeBetweenAutoGetPoint);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(LocationService.isServiceRunning){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsHelper.hasPermissions(this, permissions)) {
            Log.d("Permission", "Accordé");
        } else {
            Log.d("Permission", "Refusé");
        }
    }
}