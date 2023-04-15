package fr.insset.ccm.m1.sag.travelogue.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Location;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.db.Travel;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private FirebaseAuth mAuth;

    // [START EXCLUDE]
    // [START maps_poly_activity_get_map_async]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
    }
    // [END maps_poly_activity_get_map_async]

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
    // [END EXCLUDE]
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        // [START maps_poly_activity_add_polyline_set_tag]
        // [START maps_poly_activity_add_polyline]
        State state = new State(mAuth.getCurrentUser().getUid());



        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true));

        List<LatLng> listLatLng = new ArrayList<>();
        Travel travel = new Travel(mAuth.getCurrentUser().getUid());

        state.getCurrentTravel(dataCurrentTravel -> {
            travel.getPoints(data -> {
                Log.d("SIZE", String.valueOf(data.length()));
                for(int i = 0; i<data.length(); i++){
                    listLatLng.add(new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude()));
                }
                Log.d("MAP", "FINISH POINTS");
                Log.d("TEST", listLatLng.toString());

                polyline.setPoints(listLatLng);

                polyline.setTag("B");
                stylePolyline(polyline);

                // Position the map's camera near Alice Springs in the center of Australia,
                // and set the zoom factor so most of Australia shows on the screen.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.9544374, 3.2554693), 10));
                Log.d("MAP", "FINISH");
            }, dataCurrentTravel.get());
        });


    }
    // [END maps_poly_activity_on_map_ready]

    // [START maps_poly_activity_style_polyline]
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(Color.RED);
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }
    // [END maps_poly_activity_style_polyline]

}