package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;

public class Location {

    private final String id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Location(String id) {
        this.id = id;
    }

    public void addPoint(GpsPoint gpsPoint, String currentTravel) {
        Long timestampLong = System.currentTimeMillis() / 1000;
        String timestamp = timestampLong.toString();

        Map<String, Double> point = new HashMap<>();
        point.put("latitude", gpsPoint.getLatitude());
        point.put("longitude", gpsPoint.getLongitude());

        Map<String, String> data = new HashMap<>();
        data.put("linkedDataType", gpsPoint.getLinkedDataType());
        data.put("linkedData", gpsPoint.getLinkedData());

        db.collection(id)
                .document("data")
                .collection("travels")
                .document(currentTravel)
                .collection("points")
                .document(timestamp)
                .set(point);

        db.collection(id)
                .document("data")
                .collection("travels")
                .document(currentTravel)
                .collection("points")
                .document(timestamp)
                .set(data, SetOptions.merge());

    }
}
