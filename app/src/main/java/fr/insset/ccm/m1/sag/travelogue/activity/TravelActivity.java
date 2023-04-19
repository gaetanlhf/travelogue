package fr.insset.ccm.m1.sag.travelogue.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateGpx;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;

public class TravelActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private Travel travel;

    private FirebaseAuth mAuth;

    private TravelHelper travelHelper;

    private ArrayList<GpsPoint> pointsList = new ArrayList<>();

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
            case R.id.action_share:
                final int[] defaultItem = {-1};
                final String[] listItems = new String[]{"GPX", "KML"};
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Share this travel as:")
                        .setSingleChoiceItems(listItems, defaultItem[0], (dialog, which) -> {
                            if (which == 0) {
                                File shareGpxFile = new File(getCacheDir(), "export/" + travel.getTitle()+"-"+travel.getID()+".gpx");
                                try {
                                    GenerateGpx.generateGfx(shareGpxFile, travel.getTitle(), pointsList);
                                    Log.d("test",this.getPackageName()+".provider");
                                    Uri uri = FileProvider.getUriForFile(this, this.getPackageName()+".provider", shareGpxFile);
                                    Intent intent = new ShareCompat.IntentBuilder(this)
                                            .setType("application/gpx+xml")
                                            .setSubject("Sharing of GPS data of the travel entitled " + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle("Sharing of GPS data")
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Toast.makeText(this, "An error occurred...", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
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
                pointsList.add(data.get(i));
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