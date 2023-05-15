package fr.insset.ccm.m1.sag.travelogue.fragment;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import fr.insset.ccm.m1.sag.travelogue.Constants;
import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.activity.HomeActivity;
import fr.insset.ccm.m1.sag.travelogue.activity.NewTravelActivity;
import fr.insset.ccm.m1.sag.travelogue.adapter.CustomInfoWindowMarkerAdapter;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.helper.AppSettings;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateGpx;
import fr.insset.ccm.m1.sag.travelogue.helper.GenerateKml;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Location;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback {
    private final ArrayList<GpsPoint> pointsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    private TravelHelper travelHelper;
    private Travel travel;
    private View map;
    private State state;
    private Location locationDb;
    private SupportMapFragment mapFragment;
    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mapFragment.getMapAsync(HomeFragment.this);
        }
    };
    private View noCurrentTravel;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(false);
        spinner = view.findViewById(R.id.fragment_home_spinner);
        spinner.setVisibility(View.VISIBLE);
        state = new State(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        locationDb = new Location(mAuth.getCurrentUser().getUid());

        Button newTravelBtn = view.findViewById(R.id.home_fragment_start_new_travel_btn);
        noCurrentTravel = view.findViewById(R.id.home_fragment_no_trip_content);
        map = view.findViewById(R.id.fragment_home_map);
        noCurrentTravel.setVisibility(View.GONE);
        map.setVisibility(View.GONE);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_home_map);
        requireActivity().registerReceiver(updateReceiver, new IntentFilter("updateHomeFragment"));

        if (AppSettings.getTravelling() || AppSettings.getTravelling() == null) {
            travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());
            travelHelper.getTravel(data -> {
                travel = data.get();
                assert mapFragment != null;
                mapFragment.getMapAsync(this);
            }, AppSettings.getTravel());
            spinner.setVisibility(View.GONE);
            map.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
        } else if (!AppSettings.getTravelling() || AppSettings.getTravelling() == null){
            spinner.setVisibility(View.GONE);
            noCurrentTravel.setVisibility(View.VISIBLE);
        }

        newTravelBtn.setOnClickListener(v -> {
            Intent newTravelActivity = new Intent(getActivity(), NewTravelActivity.class);
            startActivity(newTravelActivity);
        });

        ((HomeActivity) requireActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.home_activity_relative_layout, new HomeFragment());
            tr.commit();
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topbar_home_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint({"NonConstantResourceId", "MissingPermission"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topbar_home_fragment_info:
                String alertString1 = "Name of the travel: " + travel.getTitle();
                String alertString2 = "Start time: " + travel.getStartDatetime();
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Information about this trip")
                        .setMessage(alertString1 + "\n" + alertString2)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.topbar_home_fragment_share:
                final String[] listItems = new String[]{"GPX", "KML"};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Share this travel as:")
                        .setItems(listItems, (dialog, which) -> {
                            if (which == 0) {
                                File shareGpxFile = new File(requireContext().getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".gpx");
                                try {
                                    GenerateGpx.generate(shareGpxFile, travel.getTitle(), pointsList);
                                    Log.d("test", requireContext().getPackageName() + ".provider");
                                    Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", shareGpxFile);
                                    Intent intent = new ShareCompat.IntentBuilder(requireContext())
                                            .setType("application/gpx+xml")
                                            .setSubject("Sharing of GPS data of the travel entitled " + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle("Sharing of GPS data")
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Toast.makeText(requireContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                                }
                            } else if (which == 1) {
                                File shareKmlFile = new File(requireContext().getCacheDir(), "export/" + travel.getTitle() + "-" + travel.getID() + ".kml");
                                try {
                                    GenerateKml.generate(shareKmlFile, travel.getTitle(), pointsList);
                                    Log.d("test", requireContext().getPackageName() + ".provider");
                                    Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", shareKmlFile);
                                    Intent intent = new ShareCompat.IntentBuilder(requireContext())
                                            .setType("application/vnd.google-earth.kml+xml")
                                            .setSubject("Sharing of GPS data of the travel entitled " + travel.getTitle())
                                            .setStream(uri)
                                            .setChooserTitle("Sharing of GPS data")
                                            .createChooserIntent()
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    Toast.makeText(requireContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            case R.id.topbar_home_fragment_add:
                final String[] listAddItem = new String[]{"GPS point", "Photo", "Text"};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Add an item to the map:")
                        .setItems(listAddItem, (dialog, which) -> {
                            if (which == 0) {
                                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(requireContext());

                                locationClient.getLastLocation()
                                        .addOnSuccessListener(location -> {
                                            // GPS location can be null if GPS is switched off
                                            if (location != null) {
                                                spinner.setVisibility(View.VISIBLE);
                                                double latitude = location.getLatitude();
                                                double longitude = location.getLongitude();
                                                GpsPoint gpsPoint = new GpsPoint(0, 0);
                                                gpsPoint.setLongitude(longitude);
                                                gpsPoint.setLatitude(latitude);
                                                locationDb.addPoint(gpsPoint, AppSettings.getTravel());
                                                spinner.setVisibility(View.GONE);
                                                mapFragment.getMapAsync(this);

                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                            e.printStackTrace();
                                        });

                            } else if (which == 1) {

                            }
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            case R.id.topbar_home_fragment_stop:
                stopLocationService();
                state.setTravelling(false);
                travelHelper.finishTravel(travel.getID());
                AppSettings.setTravelling(false);
                AppSettings.setTravel(null);
                setHasOptionsMenu(false);
                map.setVisibility(View.GONE);
                noCurrentTravel.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void stopLocationService() {
        if (LocationService.isServiceRunning) {
            Intent intent = new Intent(getContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            requireContext().startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
            Toast.makeText(getContext(), "Location service stopped", Toast.LENGTH_SHORT).show();
        }
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
        TravelHelper travelHelper = new TravelHelper(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

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
                googleMap.setInfoWindowAdapter(new CustomInfoWindowMarkerAdapter(requireActivity().getApplicationContext()));
                googleMap.setOnMarkerClickListener(marker -> {
                    Toast.makeText(requireContext().getApplicationContext(), "Click on marker " + marker.getPosition(), Toast.LENGTH_SHORT).show();
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
        polyline.setColor(SurfaceColors.SURFACE_2.getColor(requireContext()));
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}