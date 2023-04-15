package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;

<<<<<<< HEAD
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
=======
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
>>>>>>> 32e34aa (feat: track travel)

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;

public class Location {

    private final String id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Location(String id) {
        this.id = id;
    }

<<<<<<< HEAD
    public void addPoint(GpsPoint gpsPoint, String currentTravel) {
=======
    public void addPoint(GpsPoint gpsPoint, String currentTravel){
>>>>>>> 32e34aa (feat: track travel)
        Long timestampLong = System.currentTimeMillis() / 1000;
        String timestamp = timestampLong.toString();

        Map<String, Double> point = new HashMap<>();
        point.put("latitude", gpsPoint.getLatitude());
        point.put("longitude", gpsPoint.getLongitude());

        db.collection(id)
                .document("data")
                .collection("travels")
                .document(currentTravel)
                .collection("points")
                .document(timestamp)
                .set(point);

    }
}
