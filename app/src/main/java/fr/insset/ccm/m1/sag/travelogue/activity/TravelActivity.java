package fr.insset.ccm.m1.sag.travelogue.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;

public class TravelActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private Travel travel;

    private FirebaseAuth mAuth;

    private TravelHelper travelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        travel = new Travel(intent.getStringExtra("travelName"));

        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_travel);
        mAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());


        travelHelper.getTravel(data -> {

            travel = data.get();
            getSupportActionBar().setTitle(travel.getTitle());

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);

        }, intent.getStringExtra("travelName"));


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_info:
                String alertString1 = "Name of the travel: " + travel.getTitle();
                String alertString2 = "Start time: " + travel.getStartDatetime();
                String alertString3 = "End time: " + travel.getEndDatetime();
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Information about this trip")
                        .setMessage(alertString1 + "\n" + alertString2 + "\n" + alertString3)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.action_delete:
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Delete this trip")
                        .setMessage("Are you sure you want to delete this trip?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                travelHelper.deleteTravel(travel.getTitle());
                                finish();
                            }
                        })
                        .show();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_travel, menu);
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(false);

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true));

        List<LatLng> listLatLng = new ArrayList<>();
        TravelHelper travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());

        travelHelper.getPoints(data -> {
            for (int i = 0; i < data.length(); i++) {
                listLatLng.add(new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude()));
            }

            polyline.setPoints(listLatLng);

            stylePolyline(polyline);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
            Log.d("TRAVEL_ACTYIVITY", "MAP FINISH");
        }, travel.getID().toString());

    }

    private void stylePolyline(Polyline polyline) {
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setColor(SurfaceColors.SURFACE_2.getColor(this));
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }
}