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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.adapter.CustomInfoWindowMarkerAdapter;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateGpx;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateKml;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.stockage.ManageImages;

public class TravelActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private final ArrayList<GpsPoint> pointsList = new ArrayList<>();
    private Travel travel;
    private FirebaseAuth mAuth;
    private TravelHelper travelHelper;
    private Thread connectivityCheckThread;
    private volatile boolean threadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        travel = new Travel(intent.getStringExtra("travelName"));

        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        setContentView(R.layout.activity_travel);
        threadRunning = true;
        connectivityCheckThread = new Thread(() -> {
            while (threadRunning) {
                if (!NetworkConnectivityCheck.isNetworkAvailableAndConnected(this)) {
                    Intent noConnection = new Intent(this, NoConnection.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(noConnection);
                    finish();
                    break;
                }

                try {
                    Thread.sleep(2000); // Check every 2 seconds
                } catch (InterruptedException e) {
                    threadRunning = false;
                }
            }
        });
        connectivityCheckThread.start();
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

        }, intent.getStringExtra("travelId"));


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_info:
                String alertString1 = getString(R.string.info_name_travel) + travel.getTitle();
                String alertString2 = getString(R.string.info_start_time_travel) + travel.getStartDatetime();
                String alertString3 = getString(R.string.info_end_time_travel) + travel.getEndDatetime();
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.info_travel_title)
                        .setMessage(alertString1 + "\n" + alertString2 + "\n" + alertString3)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.action_share:
                final int[] defaultItem = {-1};
                final String[] listItems = new String[]{"GPX", "KML"};
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert_share_title)
                        .setItems(listItems, (dialog, which) -> {
                            if (which == 0) {
                                File shareGpxFile = new File(getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".gpx");
                                try {
                                    GenerateGpx.generate(shareGpxFile, travel.getTitle(), pointsList);
                                    Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", shareGpxFile);
                                    Intent intent = new ShareCompat.IntentBuilder(this)
                                            .setType("application/gpx+xml")
                                            .setSubject(R.string.share_subject + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle(R.string.sharing_gps_data)
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
                                }
                            } else if (which == 1) {
                                File shareKmlFile = new File(getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".kml");
                                try {
                                    GenerateKml.generate(shareKmlFile, travel.getTitle(), pointsList);
                                    Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", shareKmlFile);
                                    Intent intent = new ShareCompat.IntentBuilder(this)
                                            .setType("application/vnd.google-earth.kml+xml")
                                            .setSubject(R.string.share_subject + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle(R.string.sharing_gps_data)
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return true;
            case R.id.action_delete:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.alert_delete_travel)
                        .setMessage(R.string.alert_delete_travel_message)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mAuth.getCurrentUser() != null) {
                                    // Delete the storage
                                    ManageImages.deleteTravelStorage(mAuth.getCurrentUser().getEmail(), travel.getID());
                                }
                                travelHelper.deleteTravel(state -> {
                                    if (state.get()) {
                                        finish();
                                    }
                                }, travel.getID());
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
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true));

        List<LatLng> listLatLng = new ArrayList<>();
        TravelHelper travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());

        travelHelper.getPoints(data -> {
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    LatLng position = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
                    listLatLng.add(position);
                    pointsList.add(data.get(i));
                    googleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
                googleMap.setInfoWindowAdapter(new CustomInfoWindowMarkerAdapter(getApplicationContext()));
                googleMap.setOnMarkerClickListener(marker -> {
                    Toast.makeText(getApplicationContext(), "Click on marker " + marker.getPosition(), Toast.LENGTH_SHORT).show();
                    marker.setTitle("Test");
                    marker.showInfoWindow();
                    return false;
                });

                polyline.setPoints(listLatLng);

                stylePolyline(polyline);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
                Log.d("TRAVEL_ACTYIVITY", "MAP FINISH");
            }

        }, travel.getID());

    }

    private void stylePolyline(Polyline polyline) {
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setColor(SurfaceColors.SURFACE_2.getColor(this));
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadRunning = false;
        if (connectivityCheckThread != null) {
            connectivityCheckThread.interrupt();
        }
    }
}