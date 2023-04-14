package fr.insset.ccm.m1.sag.travelogue.helper.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Travel {
    private final String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Travel(String id) {
        this.id = id;
    }

    public void createTravel(String travelName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        Long timestampLong = System.currentTimeMillis() / 1000;
        String timestamp = timestampLong.toString();

        Map<String, Object> travel = new HashMap<>();
        travel.put("startDate", date);
        travel.put("startTime", time);
        travel.put("travelName", travelName);

        db.collection(id)
                .document("data")
                .collection("travels")
                .document(timestamp)
                .set(travel);

        Map<String, Object> updateState = new HashMap<>();
        updateState.put("isTravelling", true);
        updateState.put("currentTravel", timestamp);

        db.collection(id)
                .document("state")
                .set(updateState, SetOptions.merge());
    }
}
