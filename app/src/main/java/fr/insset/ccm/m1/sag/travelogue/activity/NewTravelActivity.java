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

import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionsHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class NewTravelActivity extends AppCompatActivity {

    private TextView travelName;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.create_new_travel));
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_new_travel);
        checkGrantedPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGrantedPermissions();
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
            if (AppSettings.getAutoGps()) {
                startLocationService(AppSettings.getTimeBetweenAutoGps());
            }
            AppSettings.setTravelling(true);
            AppSettings.setTravel(newTravelId);
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

    private void checkGrantedPermissions() {
        if (PermissionsHelper.hasPermissions(this, new String[]{ACCESS_FINE_LOCATION_PERMISSION})) {
            onPermissionGranted();
        } else {
            PermissionsHelper.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION}, LOCATION_PERMISSION_CODE);
        }
        if (PermissionsHelper.hasPermissions(this, new String[]{ACCESS_BACKGROUND_LOCATION_PERMISSION, FOREGROUND_SERVICE_PERMISSION})) {
            onPermissionGranted();
        } else {
            PermissionsHelper.requestPermissions(this, new String[]{ACCESS_BACKGROUND_LOCATION_PERMISSION, FOREGROUND_SERVICE_PERMISSION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        }
        if (PermissionsHelper.hasPermissions(this, new String[]{CAMERA_PERMISSION})) {
            onPermissionGranted();
        } else {
            PermissionsHelper.requestPermissions(this, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsHelper.hasPermissions(this, permissions)) {
            onPermissionGranted();
        } else {
            if (PermissionsHelper.shouldShowPermissionRationale(this, permissions)) {
                showPermissionRationale(requestCode);
            } else {
                onPermissionDenied(requestCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_SETTINGS_CODE) {
            if (PermissionsHelper.hasPermission(this, ACCESS_FINE_LOCATION_PERMISSION)) {
                onPermissionGranted();
            }
            if (PermissionsHelper.hasPermission(this, ACCESS_BACKGROUND_LOCATION_PERMISSION)) {
                onPermissionGranted();
            }
            if (PermissionsHelper.hasPermission(this, CAMERA_PERMISSION)) {
                onPermissionGranted();
            }
        }
    }

    private void showPermissionRationale(int requestCode) {
        PermissionsHelper.showDialog(this, "Permissions", getPermissionFromRequestCode(requestCode), "OK", (dialog, buttonType) -> {
            if (buttonType == PermissionsHelper.OnDialogCloseListener.TYPE_POSITIVE) {

                if (requestCode == PERMISSION_SETTINGS_CODE) {
                    PermissionsHelper.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION}, PERMISSION_SETTINGS_CODE);
                }

                if (requestCode == LOCATION_PERMISSION_CODE) {
                    PermissionsHelper.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION_PERMISSION, ACCESS_FINE_LOCATION_PERMISSION}, LOCATION_PERMISSION_CODE);
                }

                if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
                    PermissionsHelper.requestPermissions(this, new String[]{ACCESS_BACKGROUND_LOCATION_PERMISSION, FOREGROUND_SERVICE_PERMISSION}, BACKGROUND_LOCATION_PERMISSION_CODE);
                }

                if (requestCode == CAMERA_PERMISSION_CODE) {
                    PermissionsHelper.requestPermissions(this, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
                }

            } else {
                onPermissionDenied(requestCode);
            }
        });
    }

    private void onPermissionDenied(int requestCode) {
        PermissionsHelper.showDialog(this, "Permission Settings", getPermissionFromRequestCode(requestCode), "OK", (PermissionsHelper.OnDialogCloseListener) (dialog, buttonType) -> {
            if (buttonType == PermissionsHelper.OnDialogCloseListener.TYPE_POSITIVE) {

                if (requestCode == PERMISSION_SETTINGS_CODE) {
                    PermissionsHelper.openSettingScreen(this, PERMISSION_SETTINGS_CODE);
                }

                if (requestCode == LOCATION_PERMISSION_CODE) {
                    PermissionsHelper.openSettingScreen(this, LOCATION_PERMISSION_CODE);
                }

                if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
                    PermissionsHelper.openSettingScreen(this, BACKGROUND_LOCATION_PERMISSION_CODE);
                }

                if (requestCode == CAMERA_PERMISSION_CODE) {
                    PermissionsHelper.openSettingScreen(this, CAMERA_PERMISSION_CODE);
                }
            }
        });
    }

    private void onPermissionGranted() {

    }

    public String getPermissionFromRequestCode(int requestCode) {
        String str;
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_CODE:
                str = "This app require location permission!";
                break;
            case Constants.BACKGROUND_LOCATION_PERMISSION_CODE:
                str = "This app require background location permission!";
                break;
            case CAMERA_PERMISSION_CODE:
                str = "This app require camera permission!";
                break;
            default:
                str = "Grant background location permission from settings screen!";
        }
        return str;
    }
}