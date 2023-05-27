package fr.insset.ccm.m1.sag.travelogue.activity;

import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_BACKGROUND_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_COARSE_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.ACCESS_FINE_LOCATION_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.BACKGROUND_LOCATION_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.CAMERA_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.CAMERA_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.FOREGROUND_SERVICE_PERMISSION;
import static fr.insset.ccm.m1.sag.travelogue.Constants.LOCATION_PERMISSION_CODE;
import static fr.insset.ccm.m1.sag.travelogue.Constants.PERMISSION_SETTINGS_CODE;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class NewTravelActivity extends AppCompatActivity {

    private TextView travelName;

    private FirebaseAuth mAuth;
    private Thread networkCheckThread;
    private SharedPrefManager sharedPrefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_new_travel));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_new_travel);
        PermissionHelper.verifyPermissions(this);
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
        travelName = findViewById(R.id.new_travel_activity_edit_text);
        if (!TextUtils.isEmpty(travelName.getText().toString())) {
            TravelHelper newTravel = new TravelHelper(mAuth.getCurrentUser().getUid());
            String newTravelId = newTravel.createTravel(travelName.getText().toString());
            if (sharedPrefManager.getBool("AutoGps")) {
                startLocationService(sharedPrefManager.getLong("TimeBetweenAutoGps"));
            }
            Log.d("test", newTravelId);
            sharedPrefManager.updateBool("Travelling", true);
            sharedPrefManager.saveString("CurrentTravel", String.valueOf(newTravelId));
            finish();
        }
    }

    private void startLocationService(Long timeBetweenAutoGetPoint) {
        if (!LocationService.isServiceRunning) {
            if (!PermissionsHelper.hasPermission(this, ACCESS_FINE_LOCATION_PERMISSION)) {
                PermissionsHelper.requestPermissions(this, new String[]{ACCESS_BACKGROUND_LOCATION_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION, FOREGROUND_SERVICE_PERMISSION}, LOCATION_PERMISSION_CODE);
            }
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            intent.putExtra("timeBetweenUpdate", timeBetweenAutoGetPoint);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (LocationService.isServiceRunning) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!PermissionHelper.arePermissionsGranted(requestCode, permissions, grantResults)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Nosdq")
                    .setMessage("qsdsdq")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        PermissionHelper.verifyPermissions(this);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
            builder.show();
        } else {
            // Les permissions ont été accordées.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionHelper.verifyPermissions(this);
    }
}