package fr.insset.ccm.m1.sag.travelogue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.FirebaseAuth;

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
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;
import fr.insset.ccm.m1.sag.travelogue.services.LocationService;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements
        OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final ArrayList<GpsPoint> pointsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    private TravelHelper travelHelper;
    private Travel travel;

    private String currentTravel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(false);
        spinner = view.findViewById(R.id.fragment_home_spinner);
        spinner.setVisibility(View.VISIBLE);
        State state = new State(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        Button newTravelBtn = view.findViewById(R.id.home_fragment_start_new_travel_btn);
        View noCurrentTravel = view.findViewById(R.id.home_fragment_no_trip_content);
        View map = view.findViewById(R.id.fragment_home_map);
        noCurrentTravel.setVisibility(View.GONE);
        map.setVisibility(View.GONE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_home_map);


        state.isTravelling(travelling -> {
            if (travelling.get()) {
                AppSettings.setTravelling(travelling.get());
                state.getCurrentTravel(currentTravel -> {
                    travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());
                    travelHelper.getTravel(data -> {
                        travel = data.get();
                        assert mapFragment != null;
                        mapFragment.getMapAsync(this);
                    }, currentTravel.get());
                    //TODO SERVICE ACTIVATION
                    spinner.setVisibility(View.GONE);
                    map.setVisibility(View.VISIBLE);
                    setHasOptionsMenu(true);

                    //stopTravelBtn.setVisibility(View.VISIBLE);
                    //stopTravelBtn.setOnClickListener(v -> {
                    //    stopLocationService();
                    //    state.setTravelling(false);
                    //    AppSettings.setTravelling(!travelling.get());
                    //    Toast.makeText(getContext(), "isTravelling set to false", Toast.LENGTH_SHORT).show();
                    //    newTravelBtn.setVisibility(View.VISIBLE);
                    //    stopTravelBtn.setVisibility(View.GONE);
                    //});
                });


            } else {
                spinner.setVisibility(View.GONE);
                noCurrentTravel.setVisibility(View.VISIBLE);

            }
        });
        newTravelBtn.setOnClickListener(v -> {
            Intent newTravelActivity = new Intent(getActivity(), NewTravelActivity.class);
            startActivity(newTravelActivity);
        });

        ((HomeActivity) getActivity()).setFragmentRefreshListener(() -> {
            FragmentTransaction tr = getParentFragmentManager().beginTransaction();
            tr.replace(R.id.home_activity_relative_layout, new HomeFragment());
            tr.commit();
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topbar_home_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void stopLocationService() {
        if (LocationService.isServiceRunning) {
            Intent intent = new Intent(getContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent); //remplacé par startService car stopService ne fonctionne pas (wtf)
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
                googleMap.setInfoWindowAdapter(new CustomInfoWindowMarkerAdapter(getActivity().getApplicationContext()));
                googleMap.setOnMarkerClickListener(marker -> {
                    Toast.makeText(getActivity().getApplicationContext(), "Click on marker " + marker.getPosition(), Toast.LENGTH_SHORT).show();
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
        polyline.setColor(SurfaceColors.SURFACE_2.getColor(getActivity()));
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }
}