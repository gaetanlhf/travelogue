package fr.insset.ccm.m1.sag.travelogue.helper.db;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public String createTravel(String travelName) {
        Long timestampLong = System.currentTimeMillis() / 1000;
        String timestamp = timestampLong.toString();

        Map<String, Object> travel = new HashMap<>();
        travel.put("travelName", travelName);
        travel.put("endTimestamp", "");
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

        return timestamp;
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
                            points.set(i, new GpsPoint(Double.parseDouble(documentSnapshot.getData().get("longitude").toString()), Double.parseDouble(documentSnapshot.getData().get("latitude").toString()), documentSnapshot.getData().get("linkedDataType").toString(), documentSnapshot.getData().get("linkedData").toString(), documentSnapshot.getId()));
                            i++;
                            //Log.d("POINTS", documentSnapshot.getId() + " => " + documentSnapshot.getData().get("latitude"));
                        }
                        callback.onCallback(points);
                    }
                });
    }

    public void getTravels(Callback2 callback2) {
        db.collection(id)
                .document("data")
                .collection("travels")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AtomicReferenceArray<Travel> travels = new AtomicReferenceArray<>(task.getResult().size());
                        int i = 0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (Boolean.parseBoolean(Objects.requireNonNull(documentSnapshot.getData().get("isFinish")).toString())) {
                                travels.set(i, new Travel(documentSnapshot.getId(), documentSnapshot.getData().get("travelName").toString(), documentSnapshot.getData().get("endTimestamp").toString()));
                            }
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
                .document(travel)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        AtomicReference<Travel> travelAtomicReference = new AtomicReference<>();
                        travelAtomicReference.set(new Travel(documentSnapshot.getId(), documentSnapshot.getData().get("travelName").toString(), documentSnapshot.getData().get("endTimestamp").toString(), (Boolean) documentSnapshot.getData().get("isFinish")));
                        callback3.onCallback3(travelAtomicReference);
                    }
                });
    }

    public void finishTravel(String travel) {
        Map<String, Object> updateTravel = new HashMap<>();
        updateTravel.put("isFinish", true);
        Long timestampLong = System.currentTimeMillis() / 1000;
        String timestamp = timestampLong.toString();
        updateTravel.put("endTimestamp", timestamp);
        db.collection(id)
                .document("data")
                .collection("travels")
                .document(travel)
                .update(updateTravel);
    }

    public void deleteTravel(Callback4 callback4, String travelId) {
        AtomicBoolean state = new AtomicBoolean(false);
        db.collection(id)
                .document("data")
                .collection("travels")
                .document(travelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getReference().delete();
                        db.collection(id)
                                .document("data")
                                .collection("travels")
                                .document(travelId)
                                .collection("points")
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            document.getReference().delete();
                                        }
                                        state.set(true);
                                        callback4.onCallback4(state);
                                    }
                                });
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

    public interface Callback4 {
        void onCallback4(AtomicBoolean state);
    }
}
