package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;
import fr.insset.ccm.m1.sag.travelogue.entity.Travel;

public class TravelHelper {
    private final String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TravelHelper(String id) {
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
        travel.put("endDate", "");
        travel.put("endTime", "");
        travel.put("isFinish", false);

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

    public void getPoints(Callback callback, String currentTravel) {
        Log.d("TRAVEL_HELPER", "get points of travel : " + currentTravel);
        db.collection(id)
                .document("data")
                .collection("travels")
                .document(currentTravel)
                .collection("points")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int i = 0;
                        AtomicReferenceArray<GpsPoint> points = new AtomicReferenceArray<>(task.getResult().size());

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            points.set(i, new GpsPoint(Double.parseDouble(documentSnapshot.getData().get("longitude").toString()), Double.parseDouble(documentSnapshot.getData().get("latitude").toString())));
                            i++;
                            //Log.d("POINTS", documentSnapshot.getId() + " => " + documentSnapshot.getData().get("latitude"));
                        }
                        callback.onCallback(points);
                    }
                });
    }

    public void getTravels(Callback2 callback2) {
        Log.d("TRAVEL_HELPER", "get travels");
        db.collection(id)
                .document("data")
                .collection("travels")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AtomicReferenceArray<Travel> travels = new AtomicReferenceArray<>(task.getResult().size());
                        int i = 0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            travels.set(i, new Travel(documentSnapshot.getData().get("travelName").toString()));
                            i++;
                        }
                        callback2.onCallback2(travels);
                    }
                });
    }

    public void getTravel(Callback3 callback3, String travel) {
        Log.d("TRAVEL_HELPER", "get travel " + travel);

        db.collection(id)
                .document("data")
                .collection("travels")
                .whereEqualTo("travelName", travel)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        AtomicReference<Travel> travelAtomicReference = new AtomicReference<>();

                        travelAtomicReference.set(new Travel(Integer.parseInt(documentSnapshot.getId()), documentSnapshot.getData().get("travelName").toString(), documentSnapshot.getData().get("startDate").toString(), documentSnapshot.getData().get("startTime").toString(), documentSnapshot.getData().get("endDate").toString(), documentSnapshot.getData().get("endTime").toString(), (Boolean) documentSnapshot.getData().get("isFinish")));
                        callback3.onCallback3(travelAtomicReference);

                    }
                });

    }

    public interface Callback {
        void onCallback(AtomicReferenceArray<GpsPoint> points);
    }

    public interface Callback2 {
        void onCallback2(AtomicReferenceArray<Travel> travels);
    }

    public interface Callback3 {
        void onCallback3(AtomicReference<Travel> travels);
    }
}
