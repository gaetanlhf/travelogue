package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;

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

        Map<String, String> point = new HashMap<>();
        point.put("latitude", String.valueOf(gpsPoint.getLatitude()));
        point.put("longitude", String.valueOf(gpsPoint.getLongitude()));
        point.put("linkedDataType", gpsPoint.getLinkedDataType());
        point.put("linkedData", gpsPoint.getLinkedData());

        db.collection(id)
                .document("data")
                .collection("travels")
                .document(currentTravel)
                .collection("points")
                .document(timestamp)
                .set(point);
    }
}
