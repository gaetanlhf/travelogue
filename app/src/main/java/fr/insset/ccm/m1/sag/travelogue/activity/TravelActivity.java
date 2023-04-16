package fr.insset.ccm.m1.sag.travelogue.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.R;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;
import fr.insset.ccm.m1.sag.travelogue.helper.db.State;
import fr.insset.ccm.m1.sag.travelogue.helper.db.TravelHelper;

public class TravelActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private Travel travel;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        travel = new Travel(intent.getStringExtra("travelName"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        mAuth = FirebaseAuth.getInstance();

        TextView travelName = findViewById(R.id.travel_name_textView);
        TextView travelStartDateTime = findViewById(R.id.start_date_time_textView);
        TextView travelEndDateTime = findViewById(R.id.end_date_time_textView);

        TravelHelper travelHelper = new TravelHelper(mAuth.getCurrentUser().getUid());

        Button button = findViewById(R.id.btn_back_view_travel);
        button.setOnClickListener(v -> {
            finish();
        });
        travelHelper.getTravel(data -> {

            travel = data.get();
            travelName.setText(travel.getTitle());
            travelStartDateTime.setText(travel.getStartDatetime());
            travelEndDateTime.setText(travel.getEndDatetime());

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }, intent.getStringExtra("travelName"));


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
            for(int i = 0; i<data.length(); i++){
                listLatLng.add(new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude()));
            }

            polyline.setPoints(listLatLng);

            stylePolyline(polyline);

            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 10));
            Log.d("TRAVEL_ACTYIVITY", "MAP FINISH");
        }, travel.getID().toString());

    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        // Get the data object stored with the polyline.
        polyline.setStartCap(new RoundCap());

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setColor(Color.RED);
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(20);
    }
}