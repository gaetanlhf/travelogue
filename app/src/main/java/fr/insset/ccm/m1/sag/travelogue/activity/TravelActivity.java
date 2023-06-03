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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.fragment.BottomSheetPoint;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateGpx;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateKml;
import fr.insset.ccm.m1.sag.travelogue.helper.NetworkConnectivityCheck;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedMethods;
import fr.insset.ccm.m1.sag.travelogue.helper.SharedPrefManager;
import fr.insset.ccm.m1.sag.travelogue.helper.TimestampDate;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.helper.google_apis.drive.SaveTravelImagesToDrive;
import fr.insset.ccm.m1.sag.travelogue.helper.storage.ManageImages;

public class TravelActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private final ArrayList<GpsPoint> pointsList = new ArrayList<>();
    private final Map<Marker, GpsPoint> markerDataMap = new HashMap<>();
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
                    Thread.sleep(Constants.TIME_CHECK_CONNECTION);
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
                String alertString2 = getString(R.string.info_start_time_travel) + TimestampDate.getDate(travel.getID());
                String alertString3 = getString(R.string.info_end_time_travel) + TimestampDate.getDate(travel.getEndTimestamp());
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
                                            .setSubject(getString(R.string.share_subject) + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle(getString(R.string.sharing_gps_data))
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
                                            .setSubject(getString(R.string.share_subject) + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle(getString(R.string.sharing_gps_data))
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
            case R.id.action_save_to_drive:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.export_images_to_drive_action_title))
                        .setMessage(getString(R.string.export_images_to_drive_modal_text))
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(R.string.export_to_drive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mAuth.getCurrentUser() != null) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String userEmail = currentUser.getEmail();
                                    String travelEndDate = travel.getEndTimestamp();
                                    AtomicReference<String> travelogueFolderID = new AtomicReference<>(SharedPrefManager.getInstance(getApplicationContext()).getString(Constants.DRIVE_FOLDER_DATABASE_KEY));

                                    State state = new State(currentUser.getUid());
                                    state.getTravelogueFolderId(travelogueFolderId -> {
                                        if (travelogueFolderId.get() != null && !travelogueFolderId.get().equals("")) {
                                            travelogueFolderID.set(travelogueFolderId.get());
                                        }
                                    });

                                    // Export images to drive
                                    new Thread(() -> {
                                        try {
                                            String travelFolderId = SaveTravelImagesToDrive.exportTravelImagesToDrive(
                                                    getApplicationContext(),
                                                    userEmail,
                                                    travelogueFolderID.get(),
                                                    travel.getID(),
                                                    travelEndDate,
                                                    travel.getTitle()
                                            );

                                            // GPX
                                            File shareGpxFile = new File(getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".gpx");
                                            GenerateGpx.generate(shareGpxFile, travel.getTitle(), pointsList);
                                            SaveTravelImagesToDrive.uploadTravelGPXandKMLFile(
                                                    getApplicationContext(),
                                                    userEmail,
                                                    shareGpxFile,
                                                    shareGpxFile.getName(),
                                                    travelFolderId
                                            );

                                            // KML
                                            File shareKmlFile = new File(getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".kml");
                                            GenerateKml.generate(shareKmlFile, travel.getTitle(), pointsList);
                                            SaveTravelImagesToDrive.uploadTravelGPXandKMLFile(
                                                    getApplicationContext(),
                                                    userEmail,
                                                    shareKmlFile,
                                                    shareKmlFile.getName(),
                                                    travelFolderId
                                            );
                                        } catch (IOException e) {
                                            SharedMethods.displayDebugLogMessage("test_drive", "Exception => " + e.getMessage());
                                        }
                                    }).start();
                                }
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

    public void onMapReady(@NonNull GoogleMap googleMap) {
        markerDataMap.clear();
        googleMap.clear();
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true));

        List<LatLng> listLatLng = new ArrayList<>();
        TravelHelper travelHelper = new TravelHelper(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        travelHelper.getPoints(data -> {
            googleMap.setOnMarkerClickListener(null);
            if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    Log.d("test", String.valueOf(data.length()));
                    LatLng position = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
                    listLatLng.add(position);
                    pointsList.add(data.get(i));
                    if (Objects.equals(data.get(i).getLinkedDataType(), "none")) {
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        markerDataMap.put(marker, data.get(i));
                    } else if (Objects.equals(data.get(i).getLinkedDataType(), "text")) {
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                        markerDataMap.put(marker, data.get(i));

                    } else if (Objects.equals(data.get(i).getLinkedDataType(), "photo")) {
                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        markerDataMap.put(marker, data.get(i));
                    }
                }
                Log.d("test", markerDataMap.toString());


                polyline.setPoints(listLatLng);
                stylePolyline(polyline);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
            }

            googleMap.setOnMarkerClickListener(marker -> {
                GpsPoint gpsPointListen = markerDataMap.get(marker);
                BottomSheetPoint bottomSheetPoint = BottomSheetPoint.newInstance(gpsPointListen.getLinkedDataType(), gpsPointListen.getLinkedData(), gpsPointListen.getLongitude(), gpsPointListen.getLatitude(), gpsPointListen.getTimestamp());
                bottomSheetPoint.show(getSupportFragmentManager(), "bottomSheetPointListen");
                marker.hideInfoWindow();
                return false;
            });

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