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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.PermissionHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class NewTravelActivity extends AppCompatActivity {

    private TextView travelName;

    private FirebaseAuth mAuth;
    private Thread connectivityCheckThread;
    private volatile boolean threadRunning = false;
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
        threadRunning = true;
        connectivityCheckThread = new Thread(() -> {
            while (threadRunning) {
                if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                    Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    break;
                }

                try {
                    Thread.sleep(Constants.TIME_CHECK_CONNECTION);
                } catch (InterruptedException e) {
                    threadRunning = false;
                }
            }
        });
        connectivityCheckThread.start();
        PermissionHelper.verifyPermissions(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
            if (!sharedPrefManager.getBool("PermissionsRequested")) {
                sharedPrefManager.updateBool("PermissionsRequested", true);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert_not_all_permissions_granted_title)
                        .setMessage(R.string.alert_not_all_permissions_granted_desc)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            PermissionHelper.verifyPermissions(this);
                            sharedPrefManager.updateBool("PermissionsRequested", false);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        });
                builder.show();
            }
        } else {
            // Les permissions ont été accordées.
            sharedPrefManager.updateBool("PermissionsRequested", false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sharedPrefManager.getBool("PermissionsRequested")) {
            PermissionHelper.verifyPermissions(this);
        }
        if (!threadRunning) {
            threadRunning = true;
            connectivityCheckThread = new Thread(() -> {
                while (threadRunning) {
                    if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                        Intent intent = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        break;
                    }

                    try {
                        Thread.sleep(Constants.TIME_CHECK_CONNECTION);
                    } catch (InterruptedException e) {
                        threadRunning = false;
                    }
                }
            });
            connectivityCheckThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
        sharedPrefManager.updateBool("PermissionsRequested", false);
    }

}